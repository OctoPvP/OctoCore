package net.octopvp.octocore.core.manager.impl;

import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardModule;
import net.octopvp.octocore.core.module.impl.scoreboard.type.Scoreboard;
import net.octopvp.octocore.core.objects.scoreboard.DefaultScoreboardHandler;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBoardManager extends Manager {
    private static final Map<UUID, Scoreboard> scoreboardMap = new ConcurrentHashMap<>();

    public static void handleJoin(Player player) {
        Scoreboard scoreboard = ScoreboardModule.createScoreboard(player)
                .setHandler(new DefaultScoreboardHandler());
        Logger.debug("Setting scoreboard for %1, scoreboard name: %2", player.getName(), scoreboard.getHandler().getClass().getSimpleName());
        scoreboard.activate();
        scoreboardMap.put(player.getUniqueId(), scoreboard);
    }

    @Override
    public void init(OctoCore plugin) {
        new ScoreboardModule().onEnable(plugin);
    }

    @Override
    public void disable() {
        ScoreboardModule.getInstance().onDisable(OctoCore.getInstance());
    }
}
