package net.octopvp.octocore.paper.listeners.redis;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.objects.RedisHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainRedisHandler {
    @Getter
    private static final Set saving = new HashSet();

    @RedisHandler(jedisAction = JedisAction.SAVE_REQUEST_SWITCH)
    public void onSaveReq(JsonObject data) {

    }

    @RedisHandler(jedisAction = JedisAction.TAG_UPDATE)
    public void onTagUpdate(JsonObject data) {
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
}
