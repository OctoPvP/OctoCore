package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.listeners.*;
import net.octopvp.octocore.paper.utils.menu.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class SetupListeners implements Setup {
    private static final Listener[] listeners = new Listener[]{new PunishmentListener(), new JoinLeaveListener(), new ChatListener(), new DeathListener(), new CommandProcessEvent(), new MenuListener()};

    @Override
    public void setup(OctoCore plugin) {
        PluginManager plm = Bukkit.getPluginManager();
        for (Listener listener : listeners) plm.registerEvents(listener, plugin);
    }

    @Override
    public void disable(OctoCore plugin) {
    }
}
