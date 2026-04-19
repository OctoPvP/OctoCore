package net.octopvp.octocore.rpg;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.octopvp.octocore.rpg.command.RPGClearCommand;
import net.octopvp.octocore.rpg.command.RPGDebugCommand;
import net.octopvp.octocore.rpg.command.RPGEnchantCommand;
import net.octopvp.octocore.rpg.command.RPGEffectCommand;
import net.octopvp.octocore.rpg.command.RPGHelpCommand;
import net.octopvp.octocore.rpg.command.RPGItemCommand;
import net.octopvp.octocore.rpg.command.RPGQuestCommand;
import net.octopvp.octocore.rpg.command.RPGReloadCommand;
import net.octopvp.octocore.rpg.command.RPGNPCCommand;
import net.octopvp.octocore.rpg.command.RPGSettingsCommand;
import net.octopvp.octocore.rpg.command.RPGStatsCommand;
import net.octopvp.octocore.rpg.enchantment.EnchantmentDurabilityManager;
import net.octopvp.octocore.rpg.enchantment.EnchantmentManager;
import net.octopvp.octocore.rpg.item.impl.DirtSword;
import net.octopvp.octocore.rpg.manager.ItemManager;
import net.octopvp.octocore.rpg.item.impl.Shortsword;
import net.octopvp.octocore.rpg.item.impl.Longsword;
import net.octopvp.octocore.rpg.item.impl.Greatsword;
import net.octopvp.octocore.rpg.item.impl.Dagger;
import net.octopvp.octocore.rpg.item.impl.BattleAxe;
import net.octopvp.octocore.rpg.item.impl.BoagItem;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.npc.CitizensProvider;
import net.octopvp.octocore.rpg.npc.NPCManager;
import net.octopvp.octocore.rpg.scoreboard.RPGScoreboardHandler;
import net.octopvp.octocore.rpg.tab.RPGTabHandler;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardModule;
import net.octopvp.octocore.rpg.listener.DamageListener;
import net.octopvp.octocore.rpg.listener.EnchantmentListener;
import net.octopvp.octocore.rpg.listener.HealingListener;
import net.octopvp.octocore.rpg.listener.ProjectileListener;
import net.octopvp.octocore.rpg.listener.ProtocolListener;
import net.octopvp.octocore.rpg.runnable.DataUpdateRunnable;
import net.octopvp.octocore.rpg.util.runnable.Tasks;
import net.octopvp.octocore.common.util.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class OctoRPG extends JavaPlugin {

    private static OctoRPG instance;
    private ItemManager itemManager;
    private RPGPlayerManager playerManager;
    private NPCManager npcManager;
    private EnchantmentManager enchantmentManager;
    private EnchantmentDurabilityManager enchantmentDurabilityManager;
    private DataUpdateRunnable dataUpdateRunnable;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        new Logger(this.getLogger(), "", (message, players) -> {
            for (UUID uuid : players) {
                org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(message);
                }
            }
        });

        Logger.info("Starting OctoRPG");
        Tasks.init(this);

        this.itemManager = new ItemManager(this);
        this.playerManager = new RPGPlayerManager(this);
        this.npcManager = new NPCManager();
        this.enchantmentManager = new EnchantmentManager(this);
        this.enchantmentDurabilityManager = new EnchantmentDurabilityManager(this);
        new DamageListener(this);
        new EnchantmentListener(this);
        new ProjectileListener(this);
        new HealingListener(this);
        ProtocolListener.register(this);
        
        this.dataUpdateRunnable = new DataUpdateRunnable();
        this.dataUpdateRunnable.runTaskTimer(this, 2L, 2L);

        this.itemManager.registerItem(new DirtSword());
        this.itemManager.registerItem(new Shortsword());
        this.itemManager.registerItem(new Longsword());
        this.itemManager.registerItem(new Greatsword());
        this.itemManager.registerItem(new Dagger());
        this.itemManager.registerItem(new BattleAxe());
        this.itemManager.registerItem(new BoagItem());

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("rpgitem", "Gives an RPG custom item", new RPGItemCommand());
            commands.register("rpgdebug", "Toggle RPG debug mode", new RPGDebugCommand());
            commands.register("rpgenchant", "Apply an RPG enchantment", new RPGEnchantCommand());
            commands.register("rpgstats", "Modify player RPG stats", new RPGStatsCommand());
            commands.register("rpgclear", "Clear RPG items from inventory", new RPGClearCommand());
            commands.register("rpgquest", "Manage player quests", new RPGQuestCommand());
            commands.register("rpgeffect", "Apply RPG status effects", new RPGEffectCommand());
            commands.register("rpgnpc", "Manage RPG NPCs", new RPGNPCCommand());
            commands.register("rpgsettings", "Open RPG settings menu", new RPGSettingsCommand());
            commands.register("rpgreload", "Reload the RPG plugin", new RPGReloadCommand());
            commands.register("rpghelp", "Show RPG help", java.util.List.of("rpg"), new RPGHelpCommand());
        });

        CitizensProvider provider = new CitizensProvider();
        this.npcManager.setProvider(provider);
        getServer().getPluginManager().registerEvents(provider, this);
        this.npcManager.registerTypesInPackage("net.octopvp.octocore.rpg.npc.impl");

        RPGTabHandler.init();

        ScoreboardModule.getInstance().setJoinHandler(player -> {
            ScoreboardModule.getInstance().setPlayerScoreboard(player, new RPGScoreboardHandler());
        });
        org.bukkit.Bukkit.getOnlinePlayers().forEach(player -> ScoreboardModule.getInstance().setPlayerScoreboard(player, new RPGScoreboardHandler()));

        Logger.info("OctoRPG has been enabled (v" + getDescription().getVersion() + ")");
    }

    public void reload() {
        reloadConfig();
        // Refresh scoreboards for everyone
        org.bukkit.Bukkit.getOnlinePlayers().forEach(player -> ScoreboardModule.getInstance().setPlayerScoreboard(player, new RPGScoreboardHandler()));
    }

    @Override
    public void onDisable() {
        Logger.info("OctoRPG has been disabled.");
        Logger.info("Equus paratur ad diem belli, sed victoria apud Dominum est.");
    }

    public static OctoRPG getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    public EnchantmentManager getEnchantmentManager() {
        return enchantmentManager;
    }

    public EnchantmentDurabilityManager getEnchantmentDurabilityManager() {
        return enchantmentDurabilityManager;
    }
}
