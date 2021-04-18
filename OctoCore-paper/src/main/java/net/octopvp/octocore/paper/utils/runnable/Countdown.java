package net.octopvp.octocore.paper.utils.runnable;

import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Countdown {

    private int time;

    protected BukkitTask task;
    protected final Plugin plugin;
    public Countdown(int time) {
        this.time = time;
        this.plugin = OctoCorePaper.getInstance();
    }
    public abstract void count(int current);
    public final void start() {
        task = new BukkitRunnable() {

            @Override
            public void run() {
                count(time);
                if (time-- <= 0) cancel();
            }

        }.runTaskTimer(plugin, 0L, 20L);
    }

}