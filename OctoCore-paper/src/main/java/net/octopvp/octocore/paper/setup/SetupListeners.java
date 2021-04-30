package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.listeners.ChatListener;
import net.octopvp.octocore.paper.listeners.CommandProcessEvent;
import net.octopvp.octocore.paper.listeners.DeathListener;
import net.octopvp.octocore.paper.listeners.JoinLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class SetupListeners implements Setup{
    private static final Listener[] listeners = new Listener[]{new JoinLeaveListener(),new ChatListener(),new DeathListener(),new CommandProcessEvent()};
    @Override
    public void setup(OctoCore plugin){
        PluginManager plm = Bukkit.getPluginManager();
        for (Listener listener : listeners)
            plm.registerEvents(listener,plugin);
        JoinLeaveListener.init();
    }

    @Override
    public void disable(OctoCore plugin) { }
}
