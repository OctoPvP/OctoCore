package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.enums.DataUpdateReason;
import org.bukkit.Bukkit;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerDataUpdatePacket extends RedisPacket {
    private JsonObject data;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        DataUpdateReason reason = DataUpdateReason.valueOf(data.get("reason").getAsString());
        switch (reason) {
            case TAGS_UPDATE_GIVE:
                String target = data.get("target").getAsString();
                String toAdd = data.get("add").getAsString();
                if (Bukkit.getPlayer(target) != null) {
                    PlayerData pdata = PlayerManager.getProfile(Bukkit.getPlayer(target).getUniqueId());
                    pdata.addTag(TagManager.getTagByName(toAdd)); //maybe get by id
                }
                break;
            case TAGS_UPDATE_REMOVE:
                String targetWho = data.get("target").getAsString();
                String toRemove = data.get("remove").getAsString();
                if (Bukkit.getPlayer(targetWho) != null) {
                    PlayerData playerData = PlayerManager.getProfile(Bukkit.getPlayer(targetWho).getUniqueId());
                    playerData.removeTag(TagManager.getTagByName(toRemove).getId());
                }
                break;
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder(data);
    }

    @Override
    public String getName() {
        return "PlayerDataUpdatePacket";
    }
}
