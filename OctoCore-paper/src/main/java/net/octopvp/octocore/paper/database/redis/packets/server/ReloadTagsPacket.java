package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.manager.impl.TagManager;

@NoArgsConstructor
public class ReloadTagsPacket extends RedisPacket {
    @Override
    public void onReceive(JsonObject data) {
        TagManager.reloadTags();
    }
}
