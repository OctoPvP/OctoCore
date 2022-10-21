package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.Logger;
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
    private TagUpdateReason type;
    private UUID uuid, tagId;

    @Override
    public void onReceive(JsonObject data) {
        Logger.debug("Tag update");
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || tagId == null) return;
        PlayerTag tag = TagManager.getTag(tagId);
        PlayerData playerData = PlayerManager.getInstance().getData(player);
        switch (type) {
            case GIVE_TAG: {
                playerData.addTag(tag);
                playerData.save();
                return;
            }
            case REMOVE_TAG: {
                playerData.removeTag(tagId);
                playerData.save();
            }
        }
    }

    public static enum TagUpdateReason {
        GIVE_TAG, REMOVE_TAG
    }
}
