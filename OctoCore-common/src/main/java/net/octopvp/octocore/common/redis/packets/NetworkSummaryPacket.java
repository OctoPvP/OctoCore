package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NetworkSummaryPacket extends RedisPacket {
    private int totalPlayers;
    private Map<String, Integer> serverPlayerCounts;
    private Map<String, List<PlayerSummary>> serverPlayers;
    private long timestamp;

    @Override
    public void onReceive(JsonObject data) {
        // Handled by implementation
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerSummary {
        private String name;
        private String ip;
    }
}
