package net.octopvp.octocore.rpg;

import net.octopvp.octocore.rpg.command.RPGItemCommand;
import net.octopvp.octocore.rpg.item.impl.DirtSword;
import net.octopvp.octocore.rpg.manager.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OctoRPG extends JavaPlugin {

    private static OctoRPG instance;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;

        this.itemManager = new ItemManager(this);
        this.itemManager.registerItem(new DirtSword());

        getCommand("rpgitem").setExecutor(new RPGItemCommand());

        getLogger().info("OctoRPG has been enabled! Ready to train some 8-Bit Warriors.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OctoRPG has been disabled.");
    }

    public static OctoRPG getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
