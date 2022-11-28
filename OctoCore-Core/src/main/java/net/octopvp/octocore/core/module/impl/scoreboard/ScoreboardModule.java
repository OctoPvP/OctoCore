package net.octopvp.octocore.core.module.impl.scoreboard;

import lombok.Getter;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.module.Module;
import net.octopvp.octocore.core.module.impl.scoreboard.type.Scoreboard;
import net.octopvp.octocore.core.module.impl.scoreboard.type.SimpleScoreboard;
import org.bukkit.entity.Player;

public final class ScoreboardModule implements Module {
    @Getter
    private static ScoreboardModule instance;

    public static Scoreboard createScoreboard(Player holder) {
        return new SimpleScoreboard(holder);
    }

    @Override
    public void onEnable(OctoCore plugin) {
        instance = this;
    }

    @Override
    public void onDisable(OctoCore plugin) {

    }
}
