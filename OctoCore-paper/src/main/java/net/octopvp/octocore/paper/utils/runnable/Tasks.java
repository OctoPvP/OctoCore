package net.octopvp.octocore.paper.utils.runnable;

import net.octopvp.octocore.paper.OctoCore;
import org.bukkit.Bukkit;

public class Tasks {

    public static void run( Runnable callable) {
        Bukkit.getScheduler().runTask(OctoCore.getInstance(), callable);
    }

    public static void runAsync( Runnable callable) {
        Bukkit.getScheduler().runTaskAsynchronously(OctoCore.getInstance(), callable);
    }

    public static void runLater( Runnable callable, long delay) {
        Bukkit.getScheduler().runTaskLater(OctoCore.getInstance(), callable, delay);
    }

    public static void runAsyncLater( Runnable callable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(OctoCore.getInstance(), callable, delay);
    }

    public static void runTimer( Runnable callable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimer(OctoCore.getInstance(), callable, delay, interval);
    }

    public static void runAsyncTimer( Runnable callable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(OctoCore.getInstance(), callable, delay, interval);
    }
}
