package net.octopvp.octocore.rpg;

import org.bukkit.plugin.java.JavaPlugin;

public class OctoRPG extends JavaPlugin {

    private static OctoRPG instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("OctoRPG has been enabled! Ready to train some 8-Bit Warriors.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OctoRPG has been disabled.");
    }

    public static OctoRPG getInstance() {
        return instance;
    }
}
