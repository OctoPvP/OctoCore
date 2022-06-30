package net.octopvp.octocore.paper.utils.tab;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.tab.entry.TabElement;

public class DefaultTabProvider implements TabProvider {
    @Override
    public TabElement getTab() {
        TabElement element = new TabElement();
        element.add(0, CC.GREEN + CC.BOLD + "OctoCore");
        return element;
    }

    @Override
    public boolean useDefaultTab() {
        return true;
    }
}
