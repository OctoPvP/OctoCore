package net.octopvp.octocore.core.listeners;

import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.object.punish.PunishData;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.MojangAPIUtil;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.listeners.redis.MainRedisHandler;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.VanishManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import net.octopvp.octocore.core.objects.OctoPermissible;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (OctoCore.isLoading()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new DisconnectReason("The server hasn't started yet!").toString());
            return;
        }
        boolean kicked = event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED;
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            if (kicked) return;
            long start = System.currentTimeMillis();
            Logger.info("Loading " + event.getName() + "'s data...");
            String name = event.getName();
            UUID uuid = event.getUniqueId();
            MojangAPIUtil.INSTANCE.getNameCache().put(uuid, name); // Bukkit#getOfflinePlayer may be incomplete on servers w/o a player cache as getting via uuid doesnt do mojang lookups
            PlayerData playerData = PlayerManager.getInstance().createProfile(uuid, name);

            Map<UUID, PunishData> punishDataCache = new HashMap<>();

            playerData.getPunishData().forceLoadActiveBansAndBlacklists();
            playerData.loadAlts(event.getAddress().getHostAddress(), punishDataCache);

            Logger.info("Checking " + event.getName() + "'s punishments...");
            long startPunish = System.currentTimeMillis();
            if (PunishModule.checkPunishments(event, playerData, name, uuid)) {
                Logger.info("Kicked " + event.getName() + " for having an active punishment. (" + (System.currentTimeMillis() - startPunish) + "ms)");
                PlayerManager.getInstance().getPlayerProfiles().remove(uuid);
                return;
            }

            Logger.info("Caching " + event.getName() + "'s data...");
            long startCache = System.currentTimeMillis();
            DataCache cache = new DataCache(uuid);
            Document cached = cache.getData();
            playerData.load(cached);
            Logger.info("Cached " + event.getName() + "'s data in " + (System.currentTimeMillis() - startCache) + "ms");

            if (PlayerManager.getInstance().getData(event.getUniqueId()) == null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new DisconnectReason("An error occurred while loading your data.\nPlease contact an administrator if this keeps happening!.").toString());
            }
            Logger.info("Loaded " + event.getName() + "'s data in " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e) {
        PlayerData data = PlayerManager.getInstance().getData(e.getPlayer());
        if (data == null) {
            e.setQuitMessage(null);
        } else e.setQuitMessage(CC.GRAY + "[" + CC.RED + "-" + CC.GRAY + "] " + data.getFormattedName(true, e.getPlayer(), true));
        unfreezePlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR) // lowest priority so that we can unload data LAST
    public void onLeaveLast(PlayerQuitEvent e) {
        PlayerManager.getInstance().leave(e.getPlayer());
        MainRedisHandler.getSaving().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        PlayerData data = PlayerManager.getInstance().getData(event.getPlayer());
        if (data == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, new DisconnectReason("An error occurred while loading your data.\nPlease contact an administrator if this keeps happening!.").toString());
            return;
        }
        PermissibleBase old = event.getPlayer().getPermissibleBase();
        PermissibleBase newBase = new OctoPermissible(event.getPlayer(), event.getPlayer().getUniqueId(), old);
        event.getPlayer().setPermissibleBase(newBase);
        if (event.getPlayer().getPermissibleBase() instanceof OctoPermissible) {
            Logger.debug("Successfully injected permissible!");
            data.loadPerms(event.getPlayer());
        } else {
            Logger.error("Could not inject permissible!");
            //PlayerManager.captureSentryEvent("Could not inject permissible!", player);
        }
        if (OctoCore.getInstance().getServerManager().isOnline(event.getPlayer().getUniqueId())) {
            data.setLastServerOn(OctoCore.getInstance().getServerManager().getOnlinePlayer(event.getPlayer().getUniqueId()).getServer());
        } else {
            data.setJoinAlert(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer() == null || !event.getPlayer().isOnline()) {
            return;
        }
        PlayerData playerData = PlayerManager.getInstance().join(event.getPlayer());

        // TODO: Componentize this
        if (playerData.isJoinVanished()) {
            Logger.info("Vanishing " + playerData.getName() + " on join.");
            event.setJoinMessage(null);
            VanishManager.getInstance().vanish(event.getPlayer(), -1, true);
        } else {
            Logger.info("Not vanishing " + playerData.getName() + " on join.");
            String joinMessage = CC.GRAY + "[" + CC.GREEN + "+" + CC.GRAY + "] " + playerData.getFormattedName(true, event.getPlayer(), true);
            event.setJoinMessage(joinMessage);
            VanishManager.getInstance().update(event.getPlayer());
        }


        /*
        Tasks.runLater(() -> {
            LunarClientAPI.getInstance().sendPacket(event.getPlayer(), new LCPacketServerUpdate("hypixel.net"));
            Player player = event.getPlayer();
            LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("Test", player.getLocation(), Color.AQUA.asRGB(), true, true));
            ModSettings.ModSetting disabled = new ModSettings.ModSetting(false, new HashMap<>());
            LunarClientAPI.getInstance().sendPacket(player, new LCPacketModSettings(
                    new ModSettings()
                            .addModSetting("Coordinates", disabled)
                            .addModSetting("textHotKey", disabled)
            ));
            for (Player player1 : LunarClientAPI.getInstance().getPlayersRunningLunarClient()) {
                player.sendMessage(CC.GREEN + player1.getName());
            }
        }, 40L);
         */
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
