package net.octopvp.octocore.paper.menus;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.menu.Button;
import net.octopvp.octocore.paper.utils.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TestMenu extends PaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.GREEN + "Test";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer,Button> buttons = new HashMap<>();
        return buttons;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        //FIXME fix small menus
        return 16;
    }
}
