package net.octopvp.octocore.master.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.octopvp.octocore.master.master.redis.LightningRedisPacket;

import java.util.Map;

@Getter
public class NetworkSummaryPacket extends LightningRedisPacket {
    private int totalPlayers;
    private Map<String, Integer> serverPlayerCounts;
    private long timestamp;

    @Override
    public void receive(JsonObject data) {
        // Master can now use this data, e.g., cache it for the web dashboard
    }
}
