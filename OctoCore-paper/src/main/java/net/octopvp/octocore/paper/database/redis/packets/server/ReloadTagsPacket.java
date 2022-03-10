package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.manager.impl.TagManager;

@NoArgsConstructor
public class ReloadTagsPacket extends RedisPacket {
    @Override
    public void onReceive(JsonObject data) throws Exception {
        TagManager.reloadTags();
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder();
    }

    @Override
    public String getName() {
        return "ReloadTagsPacket";
    }
}
