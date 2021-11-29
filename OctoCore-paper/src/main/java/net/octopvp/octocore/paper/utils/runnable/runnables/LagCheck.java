package net.octopvp.octocore.paper.utils.runnable.runnables;

import com.google.common.collect.ImmutableList;
import net.octopvp.octocore.paper.api.events.ServerLaggedOutEvent;
import net.octopvp.octocore.paper.protocol.PingAdapter;
import net.octopvp.octocore.paper.utils.PlayerUtils;
import org.bukkit.scheduler.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class LagCheck extends BukkitRunnable {
    public void run() {
        final ImmutableList<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
        if (players.size() >= 100) {
            int playersLagging = 0;
            for (final Player player : players) {
                if (PlayerUtils.isLagging(player)) {
                    ++playersLagging;
                }
            }
            final double percentage = playersLagging * 100 / players.size();
            if (Math.abs(percentage) >= 30.0) {
                Bukkit.getPluginManager().callEvent((Event)new ServerLaggedOutEvent(PingAdapter.getAveragePing()));
            }
        }
    }
}
