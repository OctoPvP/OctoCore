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
public class GlobalCommandPacket extends RedisPacket {
    @Getter
    @Setter
    private static Implementation implementation = null;
    private String command;

    @Override
    public void onReceive(JsonObject data) {
        if (implementation != null) {
            implementation.onCommand(command);
        }
    }

    public interface Implementation {
        void onCommand(String command);
    }
}
