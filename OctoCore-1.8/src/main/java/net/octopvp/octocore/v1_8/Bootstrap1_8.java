package net.octopvp.octocore.v1_8;

import lombok.Getter;
import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.OctoCore;

@Getter
public class Bootstrap1_8 extends OctoCore {
    private final BukkitServerImplementation serverImplementation = new BukkitServerImpl1_8();
}
