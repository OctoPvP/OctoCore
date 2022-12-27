package net.octopvp.octocore.core.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class AltUpdatePacket extends RedisPacket {

    private UUID uuid;
    private String name;
    private UUID altId;
    private String altName;

    @Override
    public void onReceive(JsonObject data) {
        /*
        Player player = Bukkit.getPlayer(UUID.fromString(data.get("uuid").getAsString()));

        if (player != null) {
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());

            if (playerData != null) {
                if (playerData.getAlt(UUID.fromString(data.get("altId").getAsString())) == null) {
                    PlayerData targetData = PlayerManager.getInstance().getData(UUID.fromString(data.get("altId").getAsString()));

                    if (targetData != null) {
                        playerData.getAlts().add(new Alt(UUID.fromString(data.get("altId").getAsString()),
                                data.get("altName").getAsString(), targetData.getPunishData()).updateDisplayName());
                    } else {
                        playerData.getAlts().add(new Alt(UUID.fromString(data.get("altId").getAsString()),
                                data.get("altName").getAsString(), new PlayerData(UUID.fromString(data.get("altId").getAsString()),
                                data.get("altName").getAsString()).getPunishData()).updateDisplayName());
                    }
                }
            }
        }
         */
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            if (playerData != null) {
                if (playerData.getAlt(altId) == null) {
                    PlayerData targetData = PlayerManager.getInstance().getData(altId);
                    if (targetData != null) {
                        playerData.getAlts().add(new Alt(altId, altName, targetData.getPunishData()).updateDisplayName());
                    } else {
                        playerData.getAlts().add(new Alt(altId, altName, new PlayerData(altId, altName).getPunishData()).updateDisplayName());
                    }
                }
            }
        }
    }
}
