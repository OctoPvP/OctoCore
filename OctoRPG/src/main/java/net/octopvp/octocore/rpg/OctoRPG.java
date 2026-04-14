package net.octopvp.octocore.rpg;

import net.octopvp.octocore.rpg.command.RPGItemCommand;
import net.octopvp.octocore.rpg.item.impl.DirtSword;
import net.octopvp.octocore.rpg.manager.ItemManager;
import net.octopvp.octocore.rpg.item.impl.Shortsword;
import net.octopvp.octocore.rpg.item.impl.Longsword;
import net.octopvp.octocore.rpg.item.impl.Greatsword;
import net.octopvp.octocore.rpg.item.impl.Dagger;
import net.octopvp.octocore.rpg.item.impl.BattleAxe;
import org.bukkit.plugin.java.JavaPlugin;

public class OctoRPG extends JavaPlugin {

    private static OctoRPG instance;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;

        this.itemManager = new ItemManager(this);
        this.itemManager.registerItem(new DirtSword());
        this.itemManager.registerItem(new Shortsword());
        this.itemManager.registerItem(new Longsword());
        this.itemManager.registerItem(new Greatsword());
        this.itemManager.registerItem(new Dagger());
        this.itemManager.registerItem(new BattleAxe());

        getCommand("rpgitem").setExecutor(new RPGItemCommand());

        getLogger().info("OctoRPG has been enabled");
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
