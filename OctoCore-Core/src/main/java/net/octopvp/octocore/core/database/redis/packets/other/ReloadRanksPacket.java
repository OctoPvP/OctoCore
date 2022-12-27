package net.octopvp.octocore.core.database.redis.packets.other;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.manager.impl.RankManager;

@NoArgsConstructor
public class ReloadRanksPacket extends RedisPacket {
    @Override
    public void onReceive(JsonObject data) {
        RankManager.getInstance().reloadRanks();
    }
}
