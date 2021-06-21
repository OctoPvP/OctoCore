package net.octopvp.octocore.paper.utils.menu.buttons.impl;

import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public abstract class FilterButton extends Button {
    @Override
    public void onClick(Player player, int slot, ClickType clickType) {
        super.onClick(player, slot, clickType);
        clicked(player,clickType,slot);
    }
    public abstract void clicked(Player player,ClickType type,int slot);

    @Override
    public int getSlot() {
        return 37;
    }
}
