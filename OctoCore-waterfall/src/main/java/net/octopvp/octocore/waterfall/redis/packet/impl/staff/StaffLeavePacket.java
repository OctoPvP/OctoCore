package net.octopvp.octocore.waterfall.redis.packet.impl.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import net.octopvp.octocore.common.util.json.JsonBuilder;

@AllArgsConstructor
@NoArgsConstructor
public class StaffLeavePacket extends RedisPacket {
    private String name, server;

    @Override
    public void onReceive(JsonObject data) throws Exception {
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("name", name)
                .addProperty("server", server);
    }

    @Override
    public String getName() {
        return "StaffLeavePacket";
    }
}
