package net.octopvp.octocore.paper.utils.menu.pagination;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.Button;
import net.octopvp.octocore.paper.utils.menu.button.CloseButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class TempFilterPaginatedMenu extends PaginatedMenu {
    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer,Button> buttons = new HashMap<>();
        buttons.put(7, new PlaceholderFilterButton());
        buttons.put(4, new CloseButton());
        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        return getFilteredButtons(player);
    }
    private class PlaceholderFilterButton extends Button{

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.HOPPER).name(CC.GREEN + "Filter").lore("",CC.RED + "Coming Soon","").build();
        }

        @Override
        public void click(Player player, ClickType clickType) {
            super.click(player, clickType);
            player.sendMessage(CC.RED + "This is coming soon!");
            SoundUtil.playError(player);
        }
    }
    public abstract Map<Integer, Button> getFilteredButtons(Player player);
}
