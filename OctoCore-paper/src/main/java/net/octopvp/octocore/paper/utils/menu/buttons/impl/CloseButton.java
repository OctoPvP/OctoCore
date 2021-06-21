package net.octopvp.octocore.paper.utils.menu.buttons.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CloseButton extends Button {
    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.BARRIER).name(CC.RED + "Close").build();
    }

    @Override
    public int getSlot() {
        return 40;
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType) {
        super.onClick(player, slot, clickType);
        player.getOpenInventory().close();
    }
}
