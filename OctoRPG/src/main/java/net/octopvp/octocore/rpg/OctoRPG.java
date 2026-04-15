package net.octopvp.octocore.rpg;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.octopvp.octocore.rpg.command.RPGDebugCommand;
import net.octopvp.octocore.rpg.command.RPGEnchantCommand;
import net.octopvp.octocore.rpg.command.RPGItemCommand;
import net.octopvp.octocore.rpg.item.impl.DirtSword;
import net.octopvp.octocore.rpg.manager.ItemManager;
import net.octopvp.octocore.rpg.item.impl.Shortsword;
import net.octopvp.octocore.rpg.item.impl.Longsword;
import net.octopvp.octocore.rpg.item.impl.Greatsword;
import net.octopvp.octocore.rpg.item.impl.Dagger;
import net.octopvp.octocore.rpg.item.impl.BattleAxe;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.listener.DamageListener;
import net.octopvp.octocore.rpg.listener.EnchantmentListener;
import net.octopvp.octocore.rpg.listener.ProtocolListener;
import net.octopvp.octocore.rpg.runnable.DataUpdateRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public class OctoRPG extends JavaPlugin {

    private static OctoRPG instance;
    private ItemManager itemManager;
    private RPGPlayerManager playerManager;
    private DataUpdateRunnable dataUpdateRunnable;

    @Override
    public void onEnable() {
        instance = this;

        this.itemManager = new ItemManager(this);
        this.playerManager = new RPGPlayerManager(this);
        new DamageListener(this);
        new EnchantmentListener(this);
        ProtocolListener.register(this);
        
        this.dataUpdateRunnable = new DataUpdateRunnable();
        this.dataUpdateRunnable.runTaskTimer(this, 2L, 2L);

        this.itemManager.registerItem(new DirtSword());
        this.itemManager.registerItem(new Shortsword());
        this.itemManager.registerItem(new Longsword());
        this.itemManager.registerItem(new Greatsword());
        this.itemManager.registerItem(new Dagger());
        this.itemManager.registerItem(new BattleAxe());

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("rpgitem", "Gives an RPG custom item", new RPGItemCommand());
            commands.register("rpgdebug", "Toggle RPG debug mode", new RPGDebugCommand());
            commands.register("rpgenchant", "Apply an RPG enchantment", new RPGEnchantCommand());
        });

        getLogger().info("OctoRPG has been enabled (v" + getDescription().getVersion() + ")");
    }

    @Override
    public void onDisable() {
        getLogger().info("OctoRPG has been disabled.");
        getLogger().info("Equus paratur ad diem belli, sed victoria apud Dominum est.");
    }

    public static OctoRPG getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
