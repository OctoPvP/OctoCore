package net.octopvp.octocore.paper.utils.menu.buttons.impl;

import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class FilterButton extends Button {
    @Override
    public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
        super.onClick(player, slot, clickType, event);
        clicked(player, clickType, slot);
    }

    public abstract void clicked(Player player, ClickType type, int slot);

    @Override
    public int getSlot() {
        return 37;
    }
}
