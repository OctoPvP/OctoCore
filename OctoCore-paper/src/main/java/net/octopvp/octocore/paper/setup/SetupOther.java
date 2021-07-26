package net.octopvp.octocore.paper.setup;

import lombok.Getter;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.utils.runnable.runnables.DataUpdateRunnable;
import net.octopvp.octocore.paper.utils.runnable.runnables.SyncDataUpdateRunnable;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

public class SetupOther implements Setup{
    @Getter
    private static Scoreboard scoreboard;
    @Override
    public void setup(OctoCore plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new DataUpdateRunnable(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimer(plugin, new SyncDataUpdateRunnable(), 20L, 20L);
        if(plugin.getConfig().getBoolean("health-display")){
            Logger.info("Enabling health display");
            /*
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective h = scoreboard.registerNewObjective("showhealth", "health");
            h.setDisplaySlot(DisplaySlot.BELOW_NAME);
            h.setDisplayName(CC.RED + "❤");
             */
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"scoreboard objectives add health health " + CC.RED + "❤"); //FIXME fix this
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),"scoreboard objectives setdisplay belowName health");
        }
    }

    @Override
    public void disable(OctoCore plugin) {

    }
}
