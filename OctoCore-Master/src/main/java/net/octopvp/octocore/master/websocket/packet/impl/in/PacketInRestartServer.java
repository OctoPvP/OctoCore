package net.octopvp.aetheriacoremaster.websocket.packet.impl.in;

import com.google.gson.JsonObject;
import net.octopvp.aetheriacoremaster.master.redis.impl.ServerRestartPacket;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.websocket.ServerWebSocketHandler;
import net.octopvp.aetheriacoremaster.websocket.packet.PacketIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class PacketInRestartServer extends PacketIn {
    private String server;
    private static final Logger logger = LoggerFactory.getLogger(PacketInRestartServer.class);
    @Override
    public void onReceive(JsonObject data, ServerWebSocketHandler handler, WebSocketSession session, UserModel user) {
        try {
            if (server == null || server.isEmpty()) {
                logger.error("Server name is null or empty!");
                return;
            }
            logger.info("Restarting server: " + server);
            new Thread(()-> {
                new ServerRestartPacket(server).send();
            },"RedisTask").start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
