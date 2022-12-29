package net.octopvp.octocore.v1_19;

import net.octopvp.octocore.core.ServerImplementation;
import net.octopvp.octocore.core.utils.tab.TabAdapter;

public class ServerImpl1_19 implements ServerImplementation {
    public static final ServerImpl1_19 INSTANCE = new ServerImpl1_19();
    @Override
    public TabAdapter getTabAdapter() {
        return v1_19TabAdapter.INSTANCE;
    }
}
