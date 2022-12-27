package net.octopvp.octocore.v1_8;

import net.octopvp.octocore.core.ServerImplementation;
import net.octopvp.octocore.core.utils.tab.TabAdapter;

public class ServerImpl1_8 implements ServerImplementation {

    @Override
    public TabAdapter getTabAdapter() {
        return v1_8_R3TabAdapter.INSTANCE;
    }
}
