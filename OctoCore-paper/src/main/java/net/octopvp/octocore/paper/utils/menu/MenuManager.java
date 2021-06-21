package net.octopvp.octocore.paper.utils.menu;

import lombok.Getter;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager {
    @Getter
    private static Map<UUID, Menu> openedMenus = new HashMap<>();
    @Getter
    private static Map<UUID, Menu> lastOpenedMenus = new HashMap<>();
}
