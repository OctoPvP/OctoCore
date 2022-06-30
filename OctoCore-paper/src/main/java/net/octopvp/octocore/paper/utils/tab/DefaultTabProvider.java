package net.octopvp.octocore.paper.utils.tab;

import net.octopvp.octocore.paper.utils.tab.entry.TabElement;

public class DefaultTabProvider implements TabProvider {
    @Override
    public TabElement getTab() {
        return null;
    }

    @Override
    public boolean useDefaultTab() {
        return true;
    }
}
