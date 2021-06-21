package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.module.impl.scoreboard.ScoreboardModule;
import net.octopvp.octocore.paper.module.impl.scoreboard.type.Scoreboard;
import net.octopvp.octocore.paper.scoreboard.DefaultScoreboardHandler;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBoardManager extends Manager {
    private static Map<UUID,Scoreboard> scoreboardMap = new ConcurrentHashMap<>();
    @Override
    public void init(OctoCore plugin) {
        new ScoreboardModule().onEnable(plugin);
    }

    @Override
    public void disable() {
        ScoreboardModule.getInstance().onDisable(OctoCore.getInstance());
    }
    public static void handleJoin(Player player){
        Scoreboard scoreboard = ScoreboardModule.createScoreboard(player)
                .setHandler(new DefaultScoreboardHandler());
        scoreboard.activate();
        scoreboardMap.put(player.getUniqueId(),scoreboard);
    }
}
