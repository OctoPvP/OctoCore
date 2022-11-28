package net.octopvp.octocore.core.utils.menu;

import lombok.Getter;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MenuManager {
    @Getter
    private static final Map<UUID, Menu> openedMenus = new ConcurrentHashMap<>();
    @Getter
    private static final Map<UUID, Menu> lastOpenedMenus = new ConcurrentHashMap<>();

    static {
        Tasks.runTimer(() -> Bukkit.getOnlinePlayers().forEach(player -> {
            Menu menu = getOpenedMenus().get(player.getUniqueId());
            if (menu != null) {
                if (menu.isAutoUpdate() && !menu.isUpdateAsynchronously())
                    menu.update(player);
            }
        }), 0L, 20L);
        Tasks.runAsyncTimer(() -> Bukkit.getOnlinePlayers().forEach(player -> {
            Menu menu = getOpenedMenus().get(player.getUniqueId());
            if (menu != null && menu.isAutoUpdate()) {
                if (menu.isUpdateAsynchronously())
                    menu.update(player);
            }
        }), 0L, 20L);
    }
}
