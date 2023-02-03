package net.octopvp.octocore.master.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.master.master.redis.LightningRedisPacket;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerRestartPacket extends LightningRedisPacket {
    private String server;

    @Override
    public void receive(JsonObject data) {

    }
}
