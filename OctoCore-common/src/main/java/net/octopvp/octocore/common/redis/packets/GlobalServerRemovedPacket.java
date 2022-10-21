package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@NoArgsConstructor
public class GlobalServerRemovedPacket extends RedisPacket {
    @Getter
    @Setter
    private static Implementation implementation = null;
    private String server;

    @Override
    public void onReceive(JsonObject data) {
        if (implementation != null) {
            implementation.onServerRemoved(server);
        }
    }

    public interface Implementation {
        void onServerRemoved(String server);
    }
}
