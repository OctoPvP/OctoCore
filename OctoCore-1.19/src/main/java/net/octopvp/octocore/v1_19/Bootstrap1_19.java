package net.octopvp.octocore.v1_19;

import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.BukkitServerImplementation;

public class Bootstrap1_19 extends OctoCore {
    @Override
    public BukkitServerImplementation getServerImplementation() {
        return BukkitServerImpl1_19.INSTANCE;
    }
}
