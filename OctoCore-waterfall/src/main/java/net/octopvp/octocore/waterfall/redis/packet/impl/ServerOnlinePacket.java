package net.octopvp.octocore.waterfall.redis.packet.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@NoArgsConstructor
public class ServerOnlinePacket extends RedisPacket {
    private String server;

    @Override
    public void onReceive(JsonObject data) {

    }
}
