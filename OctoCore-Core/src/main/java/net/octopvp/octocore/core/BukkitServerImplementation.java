package net.octopvp.octocore.core;

import net.kyori.adventure.text.Component;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import org.bukkit.entity.Player;

public interface BukkitServerImplementation {
    ScoreboardHandler<?> getScoreboardHandler();

    void setTabHeaderFooter(Player player, Component header, Component footer); // TODO: figure out how to use either String or Component w/ generics
}
