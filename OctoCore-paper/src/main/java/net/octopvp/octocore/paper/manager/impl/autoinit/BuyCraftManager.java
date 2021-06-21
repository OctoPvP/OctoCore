package net.octopvp.octocore.paper.manager.impl.autoinit;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import org.bukkit.scheduler.BukkitRunnable;

public class BuyCraftManager extends Manager {
    @Override
    public void init(OctoCore plugin) {
        if (OctoCore.isMaster()){

        }
    }

    @Override
    public void disable() {

    }
    //X-Tebex-Secret - db46dfbce3a16588ae8e062fe8a8378ed5f13e75
    public class BuyCraftRunnable extends BukkitRunnable{

        @Override
        public void run() {
        }
    }
}
