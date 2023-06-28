package net.octopvp.octocore.core.database.redis.packets.other.grant;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddGrantPacket extends RedisPacket {
    private String target;
    private UUID targetId;
    private Grant data;

    @Override
    public void onReceive(JsonObject data) {
        if (Bukkit.getPlayer(targetId) != null) {
            PlayerData playerData = PlayerManager.getInstance().getData(targetId);
            playerData.getGrants().add(this.data);
            playerData.loadPerms(Bukkit.getPlayer(targetId));
            playerData.save();
        }
    }
}
