package net.octopvp.octocore.v1_21;

import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.OctoCore;

public class Bootstrap1_21 extends OctoCore {
    @Override
    public BukkitServerImplementation getServerImplementation() {
        return BukkitServerImpl1_21.INSTANCE;
    }
}
