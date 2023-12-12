package net.octopvp.octocore.v1_20;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.module.impl.scoreboard.DefaultComponentScoreboardHandler;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import net.octopvp.octocore.v1_20.listener.VanishListener_1_20;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitServerImpl1_20 implements BukkitServerImplementation {
    public static final BukkitServerImpl1_20 INSTANCE = new BukkitServerImpl1_20();

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new VanishListener_1_20(), OctoCore.getInstance());
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
}
