package net.octopvp.octocore.core.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class MessagePacket extends RedisPacket {
    private String message, from, to;
    private UUID fromId, toId;

    @Override
    public void onReceive(JsonObject data) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(fromId)) { // sender
                player.sendMessage(Lang.MSG_PLAYER_TO.getMsg(to, message));
                PlayerData d = PlayerManager.getInstance().getData(player);
                if (d != null) {
                    d.setLastMessaged(toId);
                }
                continue;
            }
            if (player.getUniqueId().equals(toId)) { // receiver
                player.sendMessage(Lang.MSG_PLAYER.getMsg(from, message));
                PlayerData d = PlayerManager.getInstance().getData(player);
                if (d != null) {
                    if (d.getMessageSettings().isSoundsEnabled()) {
                        SoundUtil.playPing(player);
                    }
                    d.setLastMessaged(fromId);
                }
                continue;
            }

            if (player.hasPermission(Permissions.SOCIAL_SPY)) {
                PlayerData d = PlayerManager.getInstance().getData(player.getUniqueId());
                if (d.isSocialSpy()) {
                    player.sendMessage(Lang.SOCIAL_SPY.getMsg(from, to, message));
                }
            }
        }
    }

}
