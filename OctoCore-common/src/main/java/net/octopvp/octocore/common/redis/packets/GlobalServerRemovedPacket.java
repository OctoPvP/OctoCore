package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.aetheriacore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@NoArgsConstructor
public class GlobalServerRemovedPacket extends RedisPacket {
    private String server;

    @Getter
    @Setter
    private static Implementation implementation = null;

    @Override
    public void onReceive(JsonObject data) {
        if (implementation != null) {
            implementation.onServerRemoved(server);
        }
    }

    @Override
    public String getType() {
        return "GLOBAL_SERVER_REMOVED";
    }

    public interface Implementation {
        void onServerRemoved(String server);
    }
}
