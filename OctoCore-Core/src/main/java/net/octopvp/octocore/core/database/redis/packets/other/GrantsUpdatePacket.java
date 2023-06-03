package net.octopvp.octocore.core.database.redis.packets.other;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
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
            playerData.getData();
        }
    }
}
