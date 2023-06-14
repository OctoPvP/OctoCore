package net.octopvp.octocore.core.setup;

import lombok.Getter;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.scoreboard.Scoreboard;

public class SetupOther implements Setup {
    @Getter
    private static Scoreboard scoreboard;

    @Override
    public void setup(OctoCore plugin) {
        //Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new DataUpdateThread(), 20L, 20L);
            /*
        if (plugin.getConfig().getBoolean("health-display")) {
            Logger.info("Enabling health display");
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective h = scoreboard.registerNewObjective("showhealth", "health");
            h.setDisplaySlot(DisplaySlot.BELOW_NAME);
            h.setDisplayName(CC.RED + "❤");
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "scoreboard objectives add health health " + CC.RED + "❤"); //FIXME fix this
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "scoreboard objectives setdisplay belowName health");
        }
             */
    }

    @Override
    public void disable(OctoCore plugin) {

    }
}
