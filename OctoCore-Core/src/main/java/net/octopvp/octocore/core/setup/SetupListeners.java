package net.octopvp.octocore.core.setup;

import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class SetupListeners implements Setup {
    private static final Listener[] listeners = new Listener[]{new PunishmentListener(), new JoinLeaveListener(), new ChatListener(), new DeathListener()};

    @Override
    public void setup(OctoCore plugin) {
        PluginManager plm = Bukkit.getPluginManager();
        for (Listener listener : listeners) plm.registerEvents(listener, plugin);
    }

    @Override
    public void disable(OctoCore plugin) {
    }
}
