package net.octopvp.octocore.waterfall;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.octopvp.octocore.waterfall.commands.LobbyCommand;
import net.octopvp.octocore.waterfall.listeners.KickEvent;

public final class OctoCoreWaterfall extends Plugin {
    @Getter
    private static OctoCoreWaterfall instance;

    @Override
    public void onEnable() {
        instance = this;
        getProxy().getPluginManager().registerCommand(this,new LobbyCommand());
        getProxy().getPluginManager().registerListener(this,new KickEvent());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
