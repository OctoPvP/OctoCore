package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.atomic.AtomicReference;

public class PunishmentListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());

        if (playerData == null) return;

        AtomicReference<IPunishment> ipmute = new AtomicReference<>();
        playerData.getAlts().forEach(alt -> {
            IPunishment punishment = alt.getPunishData().getActiveMute();
            if (punishment != null) {
                ipmute.set(alt.getPunishData().getActiveMute());
            }
        });
        if (ipmute.get() != null) {
            IPunishment mute = ipmute.get();
            event.setCancelled(true);

            if (mute.isPermanent()) {
                player.sendMessage(Lang.MUTE_CANT_TALK_PERM.toString());
            } else {
                player.sendMessage(Lang.MUTE_CANT_TALK_TEMP.getMsg(mute.getNiceExpire()));
            }
            return;
        }

        if (!playerData.getPunishData().isMuted()) return;

        IPunishment mute = playerData.getPunishData().getActiveMute();
        event.setCancelled(true);

        if (mute.isPermanent()) {
            player.sendMessage(Lang.MUTE_CANT_TALK_PERM.toString());
        } else {
            player.sendMessage(Lang.MUTE_CANT_TALK_TEMP.getMsg(mute.getNiceExpire()));
        }
    }
}
