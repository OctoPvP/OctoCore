package net.octopvp.octocore.core.utils.tab;

import net.octopvp.octocore.core.utils.tab.entry.TabElement;

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
