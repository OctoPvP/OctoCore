package net.octopvp.octocore.v1_21;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.module.impl.scoreboard.DefaultComponentScoreboardHandler;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import net.octopvp.octocore.v1_21.listener.ChatListener;
import net.octopvp.octocore.v1_21.listener.VanishListener_1_21;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitServerImpl1_21 implements BukkitServerImplementation {
    public static final BukkitServerImpl1_21 INSTANCE = new BukkitServerImpl1_21();

    @Override
    public void onLoad() {
        System.setProperty("octocore.newChatFormatting", "true");
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new VanishListener_1_21(), OctoCore.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), OctoCore.getInstance());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public ScoreboardHandler<?> getScoreboardHandler() {
        return new DefaultComponentScoreboardHandler();
    }

    @Override
    public void setTabHeaderFooter(Player player, Component header, Component footer) {
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    public void sendActionBar(Player player, Component message) {
        player.sendActionBar(message);
    }

    @Override
    public void updatePlayerCommands(Player player) {
        player.updateCommands();
    }

    @Override
    public void sendTitle(Player player, Title title) {
        player.showTitle(title);
    }

    @Override
    public Component getPlayerDisplayName(Player player) {
        return player.displayName();
    }
}