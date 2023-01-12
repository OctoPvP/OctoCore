package net.octopvp.octocore.v1_8;

import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.utils.tab.TabAdapter;

public class BukkitServerImpl1_8 implements BukkitServerImplementation {

    @Override
    public TabAdapter getTabAdapter() {
        return v1_8_R3TabAdapter.INSTANCE;
    }
}
