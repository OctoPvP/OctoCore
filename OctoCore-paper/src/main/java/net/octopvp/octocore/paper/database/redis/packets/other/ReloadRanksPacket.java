package net.octopvp.octocore.paper.database.redis.packets.other;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.manager.impl.RankManager;

@NoArgsConstructor
public class ReloadRanksPacket extends RedisPacket {
    @Override
    public void onReceive(JsonObject data) {
        RankManager.getInstance().reloadRanks();
    }

    @Override
    public JsonBuilder getData() {
        return null;
    }

    @Override
    public String getName() {
        return "ReloadTanksPacket";
    }
}
