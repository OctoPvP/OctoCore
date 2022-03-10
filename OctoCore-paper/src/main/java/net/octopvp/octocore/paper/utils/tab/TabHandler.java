package net.octopvp.octocore.paper.utils.tab;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.objects.maps.pair.PairMap;
import net.octopvp.octocore.paper.utils.tab.item.TabItem;
import net.octopvp.octocore.paper.utils.tab.tablist.TabList;
import org.bukkit.entity.Player;

public interface TabHandler {
    PairMap<Integer, Integer, TabItem> getTabItems(Player p);

    String getHeader(Player player);

    String getFooter(Player player);

    default TabList getTab(Player player) {
        return OctoCore.getTab().getTabList(player);
    }
}
