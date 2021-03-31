package net.octopvp.octocore.waterfall;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public final class OctoCoreWaterfall extends Plugin {
    @Getter
    private static OctoCoreWaterfall instance;

    @Override
    public void onEnable() {
        instance = this;
        getProxy().getPluginManager().registerCommand(this, new GetUuid("guuidbungee"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
