package net.octopvp.octocore.paper.utils.runnable;

import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Tasks {

    public static void run( Runnable callable) {
        Bukkit.getScheduler().runTask(OctoCorePaper.getInstance(), callable);
    }

    public static void runAsync( Runnable callable) {
        Bukkit.getScheduler().runTaskAsynchronously(OctoCorePaper.getInstance(), callable);
    }

    public static void runLater( Runnable callable, long delay) {
        Bukkit.getScheduler().runTaskLater(OctoCorePaper.getInstance(), callable, delay);
    }

    public static void runAsyncLater( Runnable callable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(OctoCorePaper.getInstance(), callable, delay);
    }

    public static void runTimer( Runnable callable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimer(OctoCorePaper.getInstance(), callable, delay, interval);
    }

    public static void runAsyncTimer( Runnable callable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(OctoCorePaper.getInstance(), callable, delay, interval);
    }
}
