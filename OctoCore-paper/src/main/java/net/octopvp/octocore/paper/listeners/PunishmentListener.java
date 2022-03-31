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
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();
        boolean kicked = false;
        if (PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(uuid) == null)
            PunishModule.getInstance().getProfileManager().createPlayerData(uuid, name);
        PunishPlayerData data = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(uuid);
        data.setLoading(true);
        String addr = event.getAddress().getHostAddress();
        data.setAddress(addr);
        data.checkForAddressChanges(addr);
        data.checkForPotentialAlts();
        if (OctoCore.getInstance().getConfig().getBoolean("punish.alts.allow-alts", true)) {
            int max = OctoCore.getInstance().getConfig().getInt("punish.alts.max", 5);
            if (max != -1) {
                if (data.getPotentialAlts().size() > max) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new DisconnectReason(Lang.TOO_MANY_ALTS_KICK_MESSAGE.getMsg(max, data.getPotentialAlts().size())).toString());
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    return;
                }
            }
        }
        data.load();
        data.getPunishData().load();

        data.getPunishData().getPunishments().forEach(punishment -> {
            if (punishment.hasExpired() && punishment.isLast()) {
                punishment.setLast(false);
                punishment.save(true);
            }
        });
        AtomicReference<Punishment> blacklist = new AtomicReference<>();
        data.getAlts().forEach(alt -> {
            PunishPlayerData altData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(alt.getUniqueId());
            if (altData != null && altData.getPunishData().isBlacklisted() && Bukkit.getPlayer(alt.getName()) != null) {
                blacklist.set(altData.getPunishData().getActiveBlacklist());
            } else {
                PunishData punishData = new PunishData(null);
                punishData.forceLoadBlacklists(alt.getUniqueId());

                if (punishData.isBlacklisted()) {
                    blacklist.set(punishData.getActiveBlacklist());
                }
            }
        });
        if (blacklist.get() != null) {
            Punishment activeBlacklist = blacklist.get();
            PunishmentListener.disallowBlacklist(event, activeBlacklist);
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            return;
        }
        if (data.getPunishData().isBlacklisted()) {
            Punishment activeBlacklist = data.getPunishData().getActiveBlacklist();
            PunishmentListener.disallowBlacklist(event, activeBlacklist);
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            return;
        }
        if (data.getPunishData().isBanned()) {
            Punishment activeBan = data.getPunishData().getActiveBan();
            boolean temp = activeBan.isTemporary();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, new DisconnectReason(Lang.PUNISH_KICK_MESSAGE.getMsg((temp ? Lang.TEMP : Lang.PERM), "BANNED", "Banned", activeBan.getAddedByName(), activeBan.getReason(), (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(activeBan.getNiceExpire(), activeBan.getNiceDuration()) : Lang.PERM_ENTRY), true)).toString());
            new PunishedJoinPacket(new JsonBuilder().addProperty("name", name).addProperty("type", "banned").addProperty("more", true).addProperty("expires", activeBan.getNiceExpire()).addProperty("addedBy", activeBan.getAddedByName())).send();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            return;
        }
        AtomicReference<Punishment> ipban = new AtomicReference<>();
        data.getAlts().forEach(alt -> {
            PunishPlayerData altData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(alt.getUniqueId());
            if (altData != null && altData.getPunishData().isIPBanned() && Bukkit.getPlayer(alt.getName()) != null) {
                ipban.set(altData.getPunishData().getActiveBan());
            } else {
                PunishData punishData = new PunishData(null);
                punishData.forceLoadBans(alt.getUniqueId());

                if (punishData.isIPBanned()) {
                    ipban.set(punishData.getActiveBan());
                }
            }
        });
        if (ipban.get() != null) {
            Punishment activeBan = ipban.get();
            boolean temp = activeBan.isTemporary();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, new DisconnectReason(Lang.PUNISH_KICK_MESSAGE.getMsg((temp ? Lang.TEMP : Lang.PERM), "BANNED", "Banned", activeBan.getAddedByName(), activeBan.getReason(), (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(activeBan.getNiceExpire(), activeBan.getNiceDuration()) : Lang.PERM_ENTRY), true)).toString());
            new PunishedJoinPacket(new JsonBuilder().addProperty("name", name).addProperty("type", "IP-Banned").addProperty("more", true).addProperty("expires", activeBan.getNiceExpire()).addProperty("addedBy", activeBan.getAddedByName())).send();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
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
