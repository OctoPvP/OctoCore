package net.octopvp.octocore.core.listeners;

import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.player.GlobalPlayerStatusUpdatePacket;
import net.octopvp.octocore.core.listeners.redis.MainRedisHandler;
import net.octopvp.octocore.core.manager.impl.PermissionManager;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.ScoreBoardManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.runnable.Tasks;
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
        if (!OctoCore.getInstance().isEnabled()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new DisconnectReason("The server hasn't started yet!").toString());
            return;
        }
        boolean kicked = event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED;
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            if (kicked) return;
            String name = event.getName();
            UUID uuid = event.getUniqueId();
            //PlayerManager.loadPData(event.getUniqueId(), event.getName(), true);

            new GlobalPlayerStatusUpdatePacket(name, false).send();

            if (PlayerManager.getInstance().getQuitting().containsKey(uuid)) {
                Bukkit.getServer().getScheduler().cancelTask(PlayerManager.getInstance().getQuitting().get(uuid));
                PlayerManager.getInstance().getQuitting().remove(uuid);
            }

            PlayerData playerData = PlayerManager.getInstance().createProfile(uuid, name);

            playerData.getPunishData().forceLoadActiveBansAndBlacklists();
            playerData.loadAlts(event.getAddress().getHostAddress());

            if (PunishModule.checkPunishments(event, playerData, name, uuid)) {
                PlayerManager.getInstance().getPlayerProfiles().remove(uuid);
                return;
            }

            DataCache cache = new DataCache(uuid);
            Document data0 = cache.getData();

            playerData.load(data0);


            if (PlayerManager.getInstance().getData(event.getUniqueId()) == null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new DisconnectReason("An error occurred while loading your data.\nPlease contact an administrator if this keeps happening!.").toString());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e) {
        PlayerManager.getInstance().leave(e.getPlayer());
        MainRedisHandler.getSaving().remove(e.getPlayer().getUniqueId());
        unfreezePlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        PlayerData data = PlayerManager.getInstance().getData(event.getPlayer());
        PermissionManager.injectPermissible(event.getPlayer(), data);
        if (OctoCore.getInstance().getServerManager().isPlayerOnline(event.getPlayer().getUniqueId())) {
            data.setLastServerOn(OctoCore.getInstance().getServerManager().getGlobalPlayer(event.getPlayer().getUniqueId()).getServer());
        } else {
            data.setJoinAlert(true);
        }
        if (data == null)
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, new DisconnectReason("An error occurred while loading your data.\nPlease contact an administrator if this keeps happening!.").toString());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) { //TODO join vanished
        if (event.getPlayer() == null || !event.getPlayer().isOnline()) {
            return;
        }
        PlayerManager.getInstance().join(event.getPlayer());

        ScoreBoardManager.handleJoin(event.getPlayer());
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
