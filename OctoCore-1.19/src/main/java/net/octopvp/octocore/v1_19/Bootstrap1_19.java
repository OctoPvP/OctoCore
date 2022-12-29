package net.octopvp.octocore.v1_19;

import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.ServerImplementation;

public class Bootstrap1_19 extends OctoCore {
    @Override
    public ServerImplementation getServerImplementation() {
        return ServerImpl1_19.INSTANCE;
    }
}
