package net.octopvp.octocore.rpg.listener;

import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.tab.RPGTabHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    public JoinListener(OctoRPG plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        // OctoCore sets the initial tab in HIGH priority.
        // We update it here in MONITOR (last) to ensure RPG stats are applied correctly on join.
        OctoCore.getInstance().getTabManager().updateTab(event.getPlayer());
    }
}
