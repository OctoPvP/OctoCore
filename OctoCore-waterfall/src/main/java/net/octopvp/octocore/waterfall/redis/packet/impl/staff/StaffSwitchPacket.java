package net.octopvp.octocore.waterfall.redis.packet.impl.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@NoArgsConstructor
public class StaffSwitchPacket extends RedisPacket {
    private String name, from, to;

    @Override
    public void onReceive(JsonObject data) {

    }
}
