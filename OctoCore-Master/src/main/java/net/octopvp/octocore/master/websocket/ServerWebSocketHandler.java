package net.octopvp.aetheriacoremaster.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.badbird5907.blib.objects.tuple.Pair;
import net.octopvp.aetheriacoremaster.config.HttpSessionConfig;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.repositories.UserRepository;
import net.octopvp.aetheriacoremaster.websocket.packet.Packet;
import net.octopvp.aetheriacoremaster.websocket.packet.PacketIn;
import net.octopvp.aetheriacoremaster.websocket.packet.PacketManager;
import net.octopvp.aetheriacoremaster.websocket.packet.PacketOut;
import net.octopvp.aetheriacoremaster.websocket.packet.impl.in.PacketInAuthorize;
import net.octopvp.aetheriacoremaster.websocket.packet.impl.out.PacketOutAuthStatus;
import net.octopvp.aetheriacoremaster.websocket.packet.impl.out.PacketOutKeepAlive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "*", maxAge = 3600)
public class ServerWebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable {
    private static final Logger logger = LoggerFactory.getLogger(ServerWebSocketHandler.class);
    private static final long MAX_AUTH_WINDOW = 5000L;
    @Getter
    private static Map<WebSocketSession, UserModel> activeSessions = new ConcurrentHashMap<>();
    @Getter
    private static Map<WebSocketSession, String> tokens = new ConcurrentHashMap<>();
    @Getter
    private static Map<WebSocketSession, Long> sessionsToAuthorize = new ConcurrentHashMap<>();
    @Autowired
    private HttpSessionConfig sessions;
    @Autowired
    private UserRepository userRepo;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Server connection opened");
        sessionsToAuthorize.put(session, System.currentTimeMillis() + MAX_AUTH_WINDOW);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Server connection closed: {}", status);
        activeSessions.remove(session);
        sessionsToAuthorize.remove(session);
        tokens.remove(session);
    }

    @Scheduled(fixedRate = 1000)
    void sendPeriodicMessages() throws IOException {
        if (true) return;
        //System.out.println("Sending keepalive");
        PacketOutKeepAlive packet = new PacketOutKeepAlive();
        for (Map.Entry<WebSocketSession, UserModel> entry : activeSessions.entrySet()) {
            if (entry.getValue() != null) {
                checkAuthenticated(entry.getKey());

                sendPacket(packet, entry.getKey());
            }
        }
        for (Map.Entry<WebSocketSession, Long> entry : sessionsToAuthorize.entrySet()) {
            if (System.currentTimeMillis() >= entry.getValue()) {
                entry.getKey().close(new CloseStatus(3000, "Did not authenticate in time."));
            }
        }
    }

    public void checkAuthenticated(WebSocketSession session) {
        if (tokens.containsKey(session)) {
            if (!sessions.isSessionValid(tokens.get(session))) {
                try {
                    session.close(new CloseStatus(3000, "Invalid JWT token."));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendPacket(PacketOut packet, WebSocketSession session) {
        //logger.info("Sending packet: " + packet.getClass().getSimpleName() + " | " + session.isOpen());
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(PacketManager.serializePacket(packet)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String request = message.getPayload();
            //logger.info("Server received: {}", request);
            boolean needsAuth = getSessionsToAuthorize().containsKey(session);
            if (needsAuth) {
                JsonObject json = JsonParser.parseString(request).getAsJsonObject();
                if (json.has("name") && !json.get("name").isJsonNull() && !json.get("name").getAsString().equals("PacketInAuthorize")) {
                    session.close(new CloseStatus(3000, "Needs to send authenticate packet first."));
                    return;
                }
            }
            Pair<PacketIn, JsonObject> pair = PacketManager.deserializePacket(request);
            PacketIn rawPacket = pair.getValue0();
            if (rawPacket == null) {
                logger.error("Received invalid message from websocket.");
                return;
            }
            //logger.info("Packet: {}", PacketManager.serializePacket(rawPacket));
            PacketIn packet = rawPacket;
            if (needsAuth) {
                if (packet instanceof PacketInAuthorize) {
                    PacketInAuthorize auth = (PacketInAuthorize) packet;
                    String token = auth.getToken();
                    logger.info("Token: {}", token);
                    if (sessions.isSessionValid(token)) {
                        logger.info("Token is valid.");
                        String userId = sessions.getUserIDFromSession(token);
                        if (userId == null) return;
                        Optional<UserModel> user = userRepo.findById(userId);
                        if (user.isPresent()) {
                            logger.info("User is present.");
                            getActiveSessions().put(session, user.get());
                            getSessionsToAuthorize().remove(session);
                            tokens.put(session, token);
                            sendPacket(new PacketOutAuthStatus(true), session);
                        } else {
                            sendPacket(new PacketOutAuthStatus(false), session);
                        }
                    }
                } else {
                    sendPacket(new PacketOutAuthStatus(false), session);
                }
            } else {
                packet.onReceive(pair.getValue1(), this, session, activeSessions.get(session));
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.close(new CloseStatus(1002, "Invalid packet."));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.info("Server transport error: {}", exception.getMessage());
    }

    @Override
    public List<String> getSubProtocols() {
        return Collections.singletonList("subprotocol.demo.websocket");
    }
}
