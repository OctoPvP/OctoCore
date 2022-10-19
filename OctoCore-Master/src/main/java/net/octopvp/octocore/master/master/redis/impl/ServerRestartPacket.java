package net.octopvp.aetheriacoremaster.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.octopvp.aetheriacore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@Getter
public class ServerRestartPacket extends RedisPacket {
    private String server;

    @Override
    public void onReceive(JsonObject data) {
    }

    @Override
    public String getType() {
        return "SERVER_RESTART";
    }
}
