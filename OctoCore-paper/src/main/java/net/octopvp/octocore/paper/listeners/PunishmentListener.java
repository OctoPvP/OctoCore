package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.staff.PunishedJoinPacket;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishData;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PunishmentListener implements Listener {
    public static void disallowBlacklist(AsyncPlayerPreLoginEvent event, Punishment punishment) {
        boolean temp = punishment.isTemporary();
        event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                new DisconnectReason(
                        Lang.PUNISH_KICK_MESSAGE.getMsg(
                                (temp ? Lang.TEMP : Lang.PERM),
                                "BLACKLISTED",
                                "Blacklisted",
                                punishment.getAddedByName(),
                                punishment.getReason(),
                                (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                        punishment.getNiceExpire(), punishment.getNiceDuration()) : Lang.PERM_ENTRY),
                                true)).toString()
        );
        new PunishedJoinPacket(new JsonBuilder().addProperty("name", event.getName()).addProperty("type", "blacklisted")).send();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PunishPlayerData playerData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(player.getUniqueId());

        if (playerData == null) return;

        AtomicReference<Punishment> ipmute = new AtomicReference<>();
        playerData.getAlts().forEach(alt -> {
            PunishPlayerData altData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(alt.getUniqueId());
            if (altData != null && altData.getPunishData().isIPMuted() && Bukkit.getPlayer(alt.getName()) != null) {
                ipmute.set(altData.getPunishData().getActiveBan());
            } else {
                PunishData punishData = new PunishData(null);
                punishData.forceLoadMutes(alt.getUniqueId());

                if (punishData.isIPMuted()) {
                    ipmute.set(punishData.getActiveMute());
                }
            }
        });
        if (ipmute.get() != null) {
            Punishment mute = ipmute.get();
            event.setCancelled(true);

            if (mute.isPermanent()) {
                player.sendMessage(Lang.MUTE_CANT_TALK_PERM.toString());
            } else {
                player.sendMessage(Lang.MUTE_CANT_TALK_TEMP.getMsg(mute.getNiceExpire()));
            }
            return;
        }

        if (!playerData.getPunishData().isMuted()) return;

        Punishment mute = playerData.getPunishData().getActiveMute();
        event.setCancelled(true);

        if (mute.isPermanent()) {
            player.sendMessage(Lang.MUTE_CANT_TALK_PERM.toString());
        } else {
            player.sendMessage(Lang.MUTE_CANT_TALK_TEMP.getMsg(mute.getNiceExpire()));
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PunishPlayerData playerData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(player.getUniqueId());
        UUID uuid = player.getUniqueId();

        if (playerData == null) return;

        Tasks.runAsync(() -> {
            playerData.save();
            PunishModule.getInstance().getProfileManager().unloadData(uuid);
        });
    }
}
