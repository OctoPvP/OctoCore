package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@NoArgsConstructor
public class ServerCrashPacket extends RedisPacket {
    private String server;
    private String log;

    @Override
    public void onReceive(JsonObject data) {
        // Usually handled by the Master or Bot, no local action needed
    }
}
