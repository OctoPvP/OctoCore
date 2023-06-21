package net.octopvp.octocore.v1_20;

import net.kyori.adventure.text.Component;
import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.module.impl.scoreboard.DefaultComponentScoreboardHandler;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import org.bukkit.entity.Player;

public class BukkitServerImpl1_20 implements BukkitServerImplementation {
    public static final BukkitServerImpl1_20 INSTANCE = new BukkitServerImpl1_20();

    @Override
    public ScoreboardHandler<?> getScoreboardHandler() {
        return new DefaultComponentScoreboardHandler();
    }

    @Override
    public void setTabHeaderFooter(Player player, Component header, Component footer) {
        player.sendPlayerListHeaderAndFooter(header, footer);
    }
}
