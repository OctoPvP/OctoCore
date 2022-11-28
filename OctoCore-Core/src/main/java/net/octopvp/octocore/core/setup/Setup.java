package net.octopvp.octocore.core.setup;

import net.octopvp.octocore.core.OctoCore;

public interface Setup {
    void setup(OctoCore plugin);

    void disable(OctoCore plugin);
}
