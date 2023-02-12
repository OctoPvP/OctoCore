package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

@NoArgsConstructor
public class ReloadRanksPacket extends RedisPacket {
    @Override
    public void onReceive(JsonObject data) {
        OctoCoreCommon.getInstance().getRankManager().reloadRanks();
    }
}
