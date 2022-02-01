package net.octopvp.octocore.paper.utils.menu.buttons.impl;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BackButton extends Button {
    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.ARROW).name(CC.GREEN + "Back").build();
    }

    @Override
    public int getSlot() {
        return 39;
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
        clicked(player, slot, clickType, event);
    }

    public abstract void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event);
    @RequiredArgsConstructor
    public static class DefaultBackButton extends BackButton {
        private final Menu menu;

        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (menu == null || menu.previous == null)
                return;
            menu.previous.open(player);
        }
    }
}
