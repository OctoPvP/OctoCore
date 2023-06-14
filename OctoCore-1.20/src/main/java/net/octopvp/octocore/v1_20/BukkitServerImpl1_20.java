package net.octopvp.octocore.v1_20;

import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.utils.tab.TabAdapter;

public class BukkitServerImpl1_20 implements BukkitServerImplementation {
    public static final BukkitServerImpl1_20 INSTANCE = new BukkitServerImpl1_20();
    @Override
    public TabAdapter getTabAdapter() {
        return v1_20TabAdapter.INSTANCE;
    }
}
