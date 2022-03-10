package net.octopvp.octocore.paper.utils.tab;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.objects.maps.pair.HashPairMap;
import net.octopvp.octocore.paper.objects.maps.pair.PairMap;
import net.octopvp.octocore.paper.utils.tab.item.TabItem;
import net.octopvp.octocore.paper.utils.tab.item.TextTabItem;
import org.bukkit.entity.Player;

public class DefaultTabHandler implements TabHandler {
    boolean a = false;

    @Override
    public PairMap<Integer, Integer, TabItem> getTabItems(Player p) {
        a = !a;
        PairMap<Integer, Integer, TabItem> map = new HashPairMap<>();
        map.put(0, 0, new TextTabItem(a ? CC.AQUA + ":D" : CC.GREEN + ":D", -1));
        return map;
    }

    @Override
    public String getHeader(Player player) {
        return CC.translate(OctoCore.getInstance().getConfig().getString("tab.header"));
    }

    @Override
    public String getFooter(Player player) {
        return CC.translate(OctoCore.getInstance().getConfig().getString("tab.footer"));
    }

}
