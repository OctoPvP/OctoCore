package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.listeners.ChatListener;
import net.octopvp.octocore.paper.listeners.JoinLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class SetupListeners implements Setup{
    public void setup(OctoCorePaper plugin){
        PluginManager plm = Bukkit.getPluginManager();
        plm.registerEvents(new JoinLeaveListener(), plugin);
        plm.registerEvents(new ChatListener(), plugin);
        JoinLeaveListener.init();
    }
}
