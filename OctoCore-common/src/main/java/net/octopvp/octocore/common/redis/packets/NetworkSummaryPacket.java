package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NetworkSummaryPacket extends RedisPacket {
    private int totalPlayers;
    private Map<String, Integer> serverPlayerCounts;
    private long timestamp;

    @Override
    public void onReceive(JsonObject data) {
        // Handled by implementation
    }
}
