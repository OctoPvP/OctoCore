package net.octopvp.octocore.v1_8;

import net.kyori.adventure.text.Component;
import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.module.impl.scoreboard.DefaultStringScoreboardHandler;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import org.bukkit.entity.Player;

public class BukkitServerImpl1_8 implements BukkitServerImplementation {
    @Override
    public ScoreboardHandler<?> getScoreboardHandler() {
        return new DefaultStringScoreboardHandler();
    }

    @Override
    public void setTabHeaderFooter(Player player, Component header, Component footer) {
        player.setPlayerListHeaderFooter(header, footer);
    }

}
