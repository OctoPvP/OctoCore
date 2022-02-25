package net.octopvp.octocore.paper.database.redis.packets.other;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.paper.manager.impl.RankManager;

@AllArgsConstructor
@NoArgsConstructor
public class ReloadRanksPacket extends RedisPacket {
    @Override
    public void onReceive(JsonObject data) throws Exception {
        RankManager.reloadRanks();
    }

    @Override
    public JsonBuilder getData() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
