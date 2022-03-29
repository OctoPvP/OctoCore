package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.GlobalPlayerDestroyEvent;
import net.octopvp.octocore.paper.database.redis.packets.staff.PunishedJoinPacket;
import net.octopvp.octocore.paper.listeners.redis.MainRedisHandler;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TabManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishData;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.objects.CachedData;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class JoinLeaveListener implements Listener {

    public static void freezePlayer(Player player) {
        Tasks.runSync(() -> {
            player.setWalkSpeed(0.0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000, 128, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, 128, true, false));
        });
    }

    public static void unfreezePlayer(Player player) {
        Tasks.runSync(() -> {
            player.setWalkSpeed(0.2F);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.SPEED);
        });
    }

    public static void init() {
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!OctoCore.getInstance().isEnabled()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new DisconnectReason("The server hasn't started yet!").toString());
            return;
        }
        UUID uuid = event.getUniqueId();
        String name = event.getName();
        boolean kicked = false;
        if (PunishModule.getInstance()
                .getProfileManager()
                .getPlayerDataFromUUID(uuid)
                == null)
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
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            new DisconnectReason(
                                    Lang.TOO_MANY_ALTS_KICK_MESSAGE.getMsg(
                                            max,
                                            data.getPotentialAlts().size()
                                    )
                            ).toString()
                    );
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
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    new DisconnectReason(
                            Lang.PUNISH_KICK_MESSAGE.getMsg(
                                    (temp ? Lang.TEMP : Lang.PERM),
                                    "BANNED",
                                    "Banned",
                                    activeBan.getAddedByName(),
                                    activeBan.getReason(),
                                    (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                            activeBan.getNiceExpire(), activeBan.getNiceDuration()) : Lang.PERM_ENTRY),
                                    true)).toString()
            );
            new PunishedJoinPacket(
                    new JsonBuilder().addProperty("name", name).addProperty("type", "banned").addProperty("more", true).addProperty("expires", activeBan.getNiceExpire()).addProperty("addedBy", activeBan.getAddedByName())
            ).send();
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
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    new DisconnectReason(
                            Lang.PUNISH_KICK_MESSAGE.getMsg(
                                    (temp ? Lang.TEMP : Lang.PERM),
                                    "BANNED",
                                    "Banned",
                                    activeBan.getAddedByName(),
                                    activeBan.getReason(),
                                    (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                            activeBan.getNiceExpire(), activeBan.getNiceDuration()) : Lang.PERM_ENTRY),
                                    true)).toString()
            );
            new PunishedJoinPacket(new JsonBuilder().addProperty("name", name).addProperty("type", "IP-Banned").addProperty("more", true).addProperty("expires", activeBan.getNiceExpire()).addProperty("addedBy", activeBan.getAddedByName())).send();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            if (kicked)
                return;
            //PlayerManager.loadPData(event.getUniqueId(), event.getName(), true);
            PlayerData playerData = PlayerManager.createPlayerData(uuid, name);

            CachedData cache = new CachedData(uuid);
            Document data0 = cache.getData();

            if (data0 != null) {
                playerData.load(data0);
            } else {
                playerData.load();
            }

            if (PlayerManager.getInstance().getData(event.getUniqueId()) == null)
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new DisconnectReason("An error occurred while loading your data.\nPlease contact an administrator if this keeps happening!.").toString());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e) {
        PlayerManager.processLeave(e.getPlayer());
        TabManager.onLeave(e.getPlayer());
        MainRedisHandler.getSaving().remove(e.getPlayer().getUniqueId());
        unfreezePlayer(e.getPlayer());
    }

    @EventHandler
    public void onGlobalPDestroyEvent(GlobalPlayerDestroyEvent e) {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        PlayerData data = PlayerManager.getInstance().getData(event.getPlayer());
        if (data == null)
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, new DisconnectReason("An error occurred while loading your data.\nPlease contact an administrator if this keeps happening!.").toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) { //TODO join vanished
        if (event.getPlayer() == null || !event.getPlayer().isOnline()) {
            return;
        }
        PlayerManager.processJoin(event.getPlayer(), event.getPlayer().getUniqueId(), event.getPlayer().getAddress().getHostString());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (MainRedisHandler.getSaving().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (MainRedisHandler.getSaving().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
