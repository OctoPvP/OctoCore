package net.octopvp.octocore.v1_8;

import lombok.Getter;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.BukkitServerImplementation;

@Getter
public class Bootstrap1_8 extends OctoCore {
    private BukkitServerImplementation serverImplementation = new BukkitServerImpl1_8();
}
