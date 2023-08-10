package net.octopvp.octocore.core.manager.impl;

import lombok.Getter;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.ChatColor;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VanishManager extends Manager implements Listener {
    @Getter
    private static VanishManager instance;

    @Getter
    private Map<UUID, Integer> vanished = new HashMap<>();

    @Override
    public void init(OctoCore plugin) {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {

    }

    public boolean isVanished(Player player) {
        return isVanished(player.getUniqueId());
    }

    public boolean isVanished(UUID uuid) {
        return vanished.containsKey(uuid);
    }

    public int getVanishPriority(SimplePlayerData playerData, boolean... skipVanished) { // gets their current vanish priority
        if ((skipVanished.length == 0 || !skipVanished[0]) && vanished.containsKey(playerData.getUuid())) return vanished.get(playerData.getUuid());
        Rank rank = playerData.getHighestRank();
        if (rank != null) {
            return rank.getWeight() <= 0 ? 1 : rank.getWeight();
        }
        return 1;
    }

    public int getCurrentVanishPriority(UUID uuid) {
        return vanished.getOrDefault(uuid, 0);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerData playerData = PlayerManager.getInstance().getData(event.getPlayer());
        if (playerData == null) return;
        if (isVanished(event.getPlayer())) {
            event.setQuitMessage(null);
            vanished.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            if (isVanished(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onMobTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (isVanished(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (isVanished(event.getEntity())) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        if (isVanished(event.getPlayer())) {
            if (event.getMessage().endsWith("\\")) {
                //remove the \
                event.setMessage(event.getMessage().substring(0, event.getMessage().length() - 1));
                return;
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Your chat message has been blocked because you are vanished. add a '\\' at the end of your message to bypass this.");
        }
    }

    public void vanish(Player player, int priority, boolean silent) {
        PlayerData data = PlayerManager.getInstance().getData(player);
        if (data == null) return;
        if (priority <= 0) priority = getVanishPriority(data);
        vanished.put(player.getUniqueId(), priority);
        update(player);
        if (!silent) {
            Bukkit.broadcastMessage(CC.GRAY + "[" + CC.RED + "-" + CC.GRAY + "] " + data.getFormattedName(true, player, true)); // TODO have games hook into this to handle it
        }
    }

    public void unvanish(Player player, boolean silent) {
        vanished.remove(player.getUniqueId());
        update(player);
        if (!silent) {
            PlayerData data = PlayerManager.getInstance().getData(player);
            if (data == null) return;
            Bukkit.broadcastMessage(CC.GRAY + "[" + CC.GREEN + "+" + CC.GRAY + "] " + data.getFormattedName(true, player, true));
        }
    }

    public boolean canSee(Player viewer, Player target) {
        PlayerData viewerData = PlayerManager.getInstance().getData(viewer);
        PlayerData targetData = PlayerManager.getInstance().getData(target);
        if (targetData == null) {
            return true;
        }
        if (viewerData == null) {
            return !targetData.isVanished();
        }
        if (!targetData.isVanished()) {
            return true;
        }
        int viewerPriority = getVanishPriority(viewerData);
        int targetPriority = getVanishPriority(targetData);
        return viewerPriority >= targetPriority;
    }

    public void update(Player player) { // TODO: optimize this O(n) code
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online == player) continue;
            PlayerData data = PlayerManager.getInstance().getData(online);
            if (data == null) continue;
            if (canSee(online, player)) {
                online.showPlayer(player);
            } else {
                online.hidePlayer(player);
            }
            if (canSee(player, online)) {
                player.showPlayer(online);
            } else {
                player.hidePlayer(online);
            }
        }
    }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }
}
