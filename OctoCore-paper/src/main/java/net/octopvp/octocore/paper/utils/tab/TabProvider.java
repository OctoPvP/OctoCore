package net.octopvp.octocore.paper.utils.tab;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.utils.tab.entry.TabElement;

public interface TabProvider {
    TabElement getTab();

    default long getInterval() {
        return 20L;
    }

    default boolean showHeader() {
        return true;
    }

    default boolean showFooter() {
        return true;
    }

    default String getHeader() {
        return OctoCore.getInstance().getTabManager().getHeader();
    }

    default String getFooter() {
        return OctoCore.getInstance().getTabManager().getFooter();
    }

    default boolean useDefaultTab() {
        return false;
    }
}
