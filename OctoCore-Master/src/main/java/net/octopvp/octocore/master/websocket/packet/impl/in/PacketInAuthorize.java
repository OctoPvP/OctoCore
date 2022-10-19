package net.octopvp.aetheriacoremaster.websocket.packet.impl.in;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.websocket.ServerWebSocketHandler;
import net.octopvp.aetheriacoremaster.websocket.packet.PacketIn;
import net.octopvp.aetheriacoremaster.websocket.packet.impl.out.PacketOutServerData;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class PacketInAuthorize extends PacketIn {
    private String token;

    @Override
    public void onReceive(JsonObject data, ServerWebSocketHandler handler, WebSocketSession session, UserModel user) {
    }
}
