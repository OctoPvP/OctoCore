package net.octopvp.octocore.core.utils.menu.buttons.impl;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PreviousPageButton extends Button {
    private final PaginatedMenu paginatedMenu;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.ARROW);
        item.setName("&aPrevious page");
        if (paginatedMenu.getPage() == 1) {
            item.lore(CC.RED + "This is the first page!");
        } else {
            item.lore(CC.GREEN + "Click to go to the last page");
        }
        return item.build();
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
        if (this.paginatedMenu.getPage() == 1) {
            player.sendMessage(CC.RED + "You're already on the first page!");
            return;
        }
        this.paginatedMenu.changePage(player, -1);
    }

    @Override
    public int getSlot() {
        return 36;
    }
}
