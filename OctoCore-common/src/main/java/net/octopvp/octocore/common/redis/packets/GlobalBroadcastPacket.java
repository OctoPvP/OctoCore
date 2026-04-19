package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GlobalBroadcastPacket extends RedisPacket {
    @Getter
    @Setter
    private static Implementation implementation = null;
    private String message;

    @Override
    public void onReceive(JsonObject data) {
        if (implementation != null) {
            implementation.onBroadcast(message);
        }
    }

    public interface Implementation {
        void onBroadcast(String message);
    }
}
