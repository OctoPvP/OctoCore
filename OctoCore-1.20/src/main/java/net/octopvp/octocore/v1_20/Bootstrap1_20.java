package net.octopvp.octocore.v1_20;

import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.OctoCore;

public class Bootstrap1_20 extends OctoCore {
    @Override
    public BukkitServerImplementation getServerImplementation() {
        return BukkitServerImpl1_20.INSTANCE;
    }
}
