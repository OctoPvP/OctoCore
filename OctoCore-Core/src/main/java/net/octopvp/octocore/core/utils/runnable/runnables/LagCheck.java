package net.octopvp.octocore.core.utils.runnable.runnables;

import com.google.common.collect.ImmutableList;
import net.octopvp.octocore.core.api.events.ServerLaggedOutEvent;
import net.octopvp.octocore.core.protocol.PingAdapter;
import net.octopvp.octocore.core.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
                Bukkit.getPluginManager().callEvent(new ServerLaggedOutEvent(PingAdapter.getAveragePing()));
            }
        }
    }
}
