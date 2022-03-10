package net.octopvp.octocore.paper.utils.runnable;

import net.octopvp.octocore.paper.OctoCore;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Countdown {

    protected final Plugin plugin = OctoCore.getInstance();
    protected BukkitTask task;
    private int time;

    public Countdown(int time) {
        this.time = time;
        start();
    }

    public abstract void count(int current);

    public final void start() {
        task = new BukkitRunnable() {

            @Override
            public void run() {
                time--;
                if (time <= -1) {
                    cancel();
                    return;
                }
                count(time);
            }

        }.runTaskTimer(plugin, 0L, 20L);
    }

}
