package net.octopvp.octocore.core.menus.impl;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.octocore.core.menus.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ListMenu extends Menu<PaginatedGui> {

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Online Players")
                .rows(6)
                .create();
    }

    @Override
    public void populateGui(PaginatedGui gui) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

        }
    }
    public GuiItem item(Player player) {
        return ItemBuilder.skull()
                .owner(player)
                .setName().asGuiItem();
    }
}
