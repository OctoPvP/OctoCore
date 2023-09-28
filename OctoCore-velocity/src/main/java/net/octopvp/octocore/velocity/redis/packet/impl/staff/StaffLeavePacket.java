package net.octopvp.octocore.velocity.redis.packet.impl.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@NoArgsConstructor
public class StaffLeavePacket extends RedisPacket {
    private String name, server;

    @Override
    public void onReceive(JsonObject data) {

    }
}
