package net.octopvp.octocore.paper.manager;

import net.octopvp.octocore.paper.OctoCore;

public interface Manager {
    OctoCore plugin = OctoCore.getInstance();
    void init(OctoCore plugin);
    void disable(OctoCore plugin);
}
