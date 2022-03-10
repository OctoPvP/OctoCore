package net.octopvp.octocore.waterfall.redis.packet.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;

@AllArgsConstructor
@NoArgsConstructor
public class ServerOnlinePacket extends RedisPacket {
    private String server;

    @Override
    public void onReceive(JsonObject data) throws Exception {

    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder().addProperty("server", server);
    }

    @Override
    public String getName() {
        return null;
    }
}
