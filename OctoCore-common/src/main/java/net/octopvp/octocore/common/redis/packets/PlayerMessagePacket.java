package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerMessagePacket extends RedisPacket {
    private UUID uuid;
    private String message;

    @Override
    public void onReceive(JsonObject data) {
        if (OctoCoreCommon.getInstance().isBungee()) {
            OctoCoreCommon.getInstance().getServerImplementation().sendMessage(uuid, message);
        }
    }
}
