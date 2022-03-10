package net.octopvp.octocore.paper.utils.menu.buttons.impl;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class NextPageButton extends Button {
    private final PaginatedMenu paginatedMenu;

    @Override
    public ItemStack getItem(Player player) {
        boolean next = this.paginatedMenu.getPage() < this.paginatedMenu.getPages(player);
        ItemBuilder item = new ItemBuilder(Material.ARROW);//next ? new ItemBuilder(Material.ARROW) : new ItemBuilder(GLASS);
        if (next) { //next page
            item.lore(
                    CC.GREEN + "Click to go to the next page"
            );
        } else item.lore(CC.RED + "This is the last page!");
        item.name(CC.GREEN + "Next Page");
        return item.build();
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
        if (!(this.paginatedMenu.getPage() < this.paginatedMenu.getPages(player))) {
            player.sendMessage(CC.RED + "You're already on the last page!");
            return;
        }
        this.paginatedMenu.changePage(player, 1);
    }

    @Override
    public int getSlot() {
        return 44;
    }
}
