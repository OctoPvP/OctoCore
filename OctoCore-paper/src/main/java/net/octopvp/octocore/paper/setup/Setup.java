package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCore;

public interface Setup {
    void setup(OctoCore plugin);

    void disable(OctoCore plugin);
}
