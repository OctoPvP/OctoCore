package net.octopvp.octocore.core.module;

import net.octopvp.octocore.core.OctoCore;

public interface Module {
    void onEnable(OctoCore plugin);

    void onDisable(OctoCore plugin);

}
