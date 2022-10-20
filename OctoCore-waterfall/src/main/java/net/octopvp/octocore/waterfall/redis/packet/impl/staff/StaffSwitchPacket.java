package net.octopvp.octocore.waterfall.redis.packet.impl.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import net.octopvp.octocore.common.util.json.JsonBuilder;

@AllArgsConstructor
@NoArgsConstructor
public class StaffSwitchPacket extends RedisPacket {
    private String name, from, to;

    @Override
    public void onReceive(JsonObject data) throws Exception {

    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("name", name)
                .addProperty("to", to)
                .addProperty("from", from);
    }


    @Override
    public String getName() {
        return "StaffSwitchPacket";
    }
}
