package net.octopvp.octocore.core.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.octocore.common.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedMenu<T extends PaginatedGui> extends Menu<T> {
    @Override
    public void populateGui(T gui, Player player) {
        if (fillBorders()) {
            gui.getFiller().fillBorder(PLACEHOLDER_ITEM);
        }
        for (GuiItem item : getItems(player)) {
            gui.addItem(item);
        }
        setButtons(true);
    }

    public abstract List<GuiItem> getItems(Player player);

    public boolean fillBorders() {
        return true;
    }

    public void setButtons(boolean... setOpt) {
        int rows = gui.getRows();
        boolean set = setOpt.length > 0 && setOpt[0];

        if (set) {
            gui.setItem(rows, 3, previousButton());
            gui.setItem(rows, 7, nextButton());
            gui.setItem(rows, 5, closeButton());
        } else {
            gui.updateItem(rows, 3, previousButton());
            gui.updateItem(rows, 7, nextButton());
            gui.updateItem(rows, 5, closeButton());
        }
    }

    public GuiItem nextButton() {
        List<Component> components = new ArrayList<>();
        int pageNum = gui.getCurrentPageNum();
        int totalPages = gui.getPagesNum();
        boolean isLastPage = pageNum + 1 > totalPages;
        if (isLastPage) {
            components.add(Component.text("This is the last page!", NamedTextColor.RED));
        } else {
            components.add(Component.text("Click to go to the next page", NamedTextColor.YELLOW));
        }
        return ItemBuilder.from(Material.ARROW)
                .name(CC.GREEN + "Next Page")
                .lore(components).asGuiItem(event -> {
                    gui.next();
                    setButtons();
                });
    }

    public GuiItem previousButton() {
        List<Component> components = new ArrayList<>();
        int pageNum = gui.getCurrentPageNum();
        boolean firstPage = pageNum - 1 == 0;
        if (firstPage) {
            components.add(Component.text("This is the first page!")
                    .color(NamedTextColor.RED));
        } else {
            components.add(Component.text("Click to go to the previous page")
                    .color(NamedTextColor.YELLOW));
        }
        return ItemBuilder.from(Material.ARROW)
                .name(CC.GREEN + "Previous Page")
                .lore(components)
                .asGuiItem(event -> {
                    gui.previous();
                    setButtons();
                });
    }
}
