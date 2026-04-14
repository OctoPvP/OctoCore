package net.octopvp.octocore.rpg.menu;

import dev.octomc.agile.builder.item.ItemBuilder;
import dev.octomc.agile.guis.GuiItem;
import dev.octomc.agile.guis.PaginatedGui;
import dev.octomc.agile.menu.Menu;
import dev.octomc.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemsMenu extends PaginatedMenu<PaginatedGui> {
    private final String title;
    private final List<CustomItem> items;

    public ItemsMenu(String title, List<CustomItem> items) {
        this.title = title;
        this.items = items;
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> guiItems = new ArrayList<>();
        for (CustomItem customItem : items) {
            ItemStack itemStack = customItem.build();
            guiItems.add(ItemBuilder.from(itemStack)
                    .lore("", CC.translate("&eLeft Click to receive 1!"), CC.translate("&eRight Click to receive 64!"))
                    .asGuiItem(event -> {
                        if (event.getClick() == ClickType.LEFT) {
                            player.getInventory().addItem(itemStack);
                            player.sendMessage(CC.translate("&aGave you 1x &f" + customItem.getName()));
                        } else if (event.getClick() == ClickType.RIGHT) {
                            ItemStack stack = itemStack.clone();
                            stack.setAmount(64);
                            player.getInventory().addItem(stack);
                            player.sendMessage(CC.translate("&aGave you 64x &f" + customItem.getName()));
                        }
                    }));
        }
        return guiItems;
    }

    @Override
    public Menu<?> getBackMenu() {
        return new MainItemsMenu();
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return dev.octomc.agile.guis.Gui.paginated()
                .title(title)
                .rows(6)
                .create();
    }
}
