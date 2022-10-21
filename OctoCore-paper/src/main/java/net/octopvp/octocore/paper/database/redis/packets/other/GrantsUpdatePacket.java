package net.octopvp.octocore.paper.database.redis.packets.other;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class GrantsUpdatePacket extends RedisPacket {
    private String name, toChange;
    private boolean add;

    @Override
    public void onReceive(JsonObject data) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            Grant grant = OctoCore.getGson().fromJson(toChange, Grant.class);
            if (add)
                playerData.getGrants().add(grant);
            else playerData.getGrants().remove(grant);
            playerData.loadPerms(player);
            playerData.save();
        }
    }
}
