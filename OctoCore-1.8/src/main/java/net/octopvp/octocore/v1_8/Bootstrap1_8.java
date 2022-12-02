package net.octopvp.octocore.v1_8;

import lombok.Getter;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.ServerImplementation;

@Getter
public class Bootstrap1_8 extends OctoCore {
    private ServerImplementation serverImplementation = new ServerImpl1_8();
}
