package net.octopvp.octocore.paper.utils.menu;

import lombok.Getter;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MenuManager {
    @Getter
    private static Map<UUID, Menu> openedMenus = new ConcurrentHashMap<>();
    @Getter
    private static Map<UUID, Menu> lastOpenedMenus = new ConcurrentHashMap<>();
    {
        Tasks.runAsyncTimer(()-> Bukkit.getOnlinePlayers().forEach(player ->{
            Menu menu = getOpenedMenus().get(player.getUniqueId());
            if (menu != null && menu.isUpdateInTask()) {
                menu.update(player);
            }
        }), 0L, 20L);
    }
}
