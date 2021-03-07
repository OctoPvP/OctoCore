package net.octopvp.octocore.waterfall;

import net.md_5.bungee.api.plugin.Plugin;

public final class OctoCoreWaterfall extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new GetUuid("guuidbungee"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
