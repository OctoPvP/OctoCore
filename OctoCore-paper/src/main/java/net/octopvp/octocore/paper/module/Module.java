package net.octopvp.octocore.paper.module;

import net.octopvp.octocore.paper.OctoCore;

public interface Module {
    void onEnable(OctoCore plugin);
    void onDisable(OctoCore plugin);

}
