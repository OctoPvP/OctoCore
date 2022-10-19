package net.octopvp.aetheriacoremaster.websocket.packet;

import com.google.gson.JsonObject;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.websocket.ServerWebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

public abstract class PacketIn extends Packet {
    @Override
    public PacketDirection getDirection() {
        return PacketDirection.IN;
    }

    public abstract void onReceive(JsonObject data, ServerWebSocketHandler handler, WebSocketSession session, UserModel user);
}
