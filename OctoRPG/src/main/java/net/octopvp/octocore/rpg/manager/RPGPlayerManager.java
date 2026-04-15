package net.octopvp.octocore.rpg.manager;

import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RPGPlayerManager implements Listener {
    private static RPGPlayerManager instance;
    private final Map<UUID, RPGPlayerData> dataMap = new ConcurrentHashMap<>();

    public RPGPlayerManager(OctoRPG plugin) {
        instance = this;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static RPGPlayerManager getInstance() {
        return instance;
    }

    public Map<UUID, RPGPlayerData> getDataMap() {
        return dataMap;
    }

    public RPGPlayerData getData(UUID uuid) {
        return dataMap.get(uuid);
    }

    public RPGPlayerData getData(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        RPGPlayerData data = new RPGPlayerData(event.getPlayer().getUniqueId());
        dataMap.put(event.getPlayer().getUniqueId(), data);
        data.join(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dataMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onHeldItemChange(PlayerItemHeldEvent event) {
        RPGPlayerData data = getData(event.getPlayer());
        if (data != null) {
            // We run it next tick because the item in hand hasn't actually changed yet in the event
            OctoRPG.getInstance().getServer().getScheduler().runTask(OctoRPG.getInstance(), () -> {
                data.update();
            });
        }
    }
}
