package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class TagUpdatePacket extends RedisPacket {
    private JsonBuilder builder;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        System.out.println("Tag update: " + OctoCore.getGson().toJson(data));
        String type = data.get("type").getAsString();
        Player player = Bukkit.getPlayer(UUID.fromString(data.get("uuid").getAsString()));
        UUID tagId = UUID.fromString(data.get("tagId").getAsString());
        if (player == null || tagId == null) return;
        PlayerTag tag = TagManager.getTag(tagId);
        PlayerData playerData = PlayerManager.getData(player);
        switch (type) {
            case "GIVE_TAG": {
                playerData.addTag(tag);
                return;
            }
            case "REMOVE_TAG": {
                playerData.removeTag(tagId);
                return;
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return builder;
    }

    @Override
    public String getName() {
        return "TagUpdatePacket";
    }
}
