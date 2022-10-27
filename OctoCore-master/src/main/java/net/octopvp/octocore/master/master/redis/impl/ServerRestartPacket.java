package net.octopvp.octocore.master.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@Getter
public class ServerRestartPacket extends RedisPacket {
    private String server;

    @Override
    public void onReceive(JsonObject data) {
    }
}
