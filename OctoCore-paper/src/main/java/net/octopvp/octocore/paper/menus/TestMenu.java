package net.octopvp.octocore.paper.menus;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import org.bukkit.entity.Player;

import java.util.List;

public class TestMenu extends PaginatedMenu {

    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + "Test";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        return null;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }
}
