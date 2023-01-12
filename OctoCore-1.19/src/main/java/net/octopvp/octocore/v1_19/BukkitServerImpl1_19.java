package net.octopvp.octocore.v1_19;

import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.utils.tab.TabAdapter;

public class BukkitServerImpl1_19 implements BukkitServerImplementation {
    public static final BukkitServerImpl1_19 INSTANCE = new BukkitServerImpl1_19();
    @Override
    public TabAdapter getTabAdapter() {
        return v1_19TabAdapter.INSTANCE;
    }
}
