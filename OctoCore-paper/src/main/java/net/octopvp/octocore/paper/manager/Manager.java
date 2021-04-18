package net.octopvp.octocore.paper.manager;

import net.octopvp.octocore.paper.OctoCorePaper;

public interface Manager {
    void init(OctoCorePaper plugin);
    void disable(OctoCorePaper plugin);
}
