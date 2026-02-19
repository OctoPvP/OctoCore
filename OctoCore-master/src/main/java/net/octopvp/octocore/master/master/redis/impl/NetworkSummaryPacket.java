package net.octopvp.octocore.master.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.master.master.redis.LightningRedisPacket;

import java.util.List;
import java.util.Map;

@Getter
public class NetworkSummaryPacket extends LightningRedisPacket {
    private int totalPlayers;
    private Map<String, Integer> serverPlayerCounts;
    private Map<String, List<PlayerSummary>> serverPlayers;
    private long timestamp;

    @Override
    public void receive(JsonObject data) {
        // Master can now use this data, e.g., cache it for the web dashboard
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerSummary {
        private String name;
        private String ip;
    }
}
