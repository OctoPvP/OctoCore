package net.octopvp.octocore.paper.utils.tab;

import net.octopvp.octocore.paper.objects.maps.pair.PairMap;
import net.octopvp.octocore.paper.utils.tab.item.TabItem;
import org.bukkit.entity.Player;

public class DefaultTabHandler implements TabHandler{
    @Override
    public PairMap<Integer, Integer, TabItem> getTabItems(Player p) {
        return null;
    }

    @Override
    public String getHeader(Player player) {
        return null;
    }

    @Override
    public String getFooter(Player player) {
        return null;
    }

}
