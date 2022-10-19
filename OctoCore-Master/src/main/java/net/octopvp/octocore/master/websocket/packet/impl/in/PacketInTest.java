package net.octopvp.aetheriacoremaster.websocket.packet.impl.in;

import com.google.gson.JsonObject;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.websocket.ServerWebSocketHandler;
import net.octopvp.aetheriacoremaster.websocket.packet.PacketIn;
import org.springframework.web.socket.WebSocketSession;

public class PacketInTest extends PacketIn {
    private String data;

    @Override
    public void onReceive(JsonObject data, ServerWebSocketHandler handler, WebSocketSession session, UserModel user) {
        System.out.println("Data: " + this.data);
    }
}
