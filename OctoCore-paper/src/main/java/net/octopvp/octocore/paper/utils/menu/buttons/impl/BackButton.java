package net.octopvp.octocore.paper.utils.menu.buttons.impl;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
    public void onClick(Player player, int slot, ClickType clickType) {
        clicked(player, slot, clickType);
    }
    public abstract void clicked(Player player,int slot,ClickType clickType);
    @RequiredArgsConstructor
    public static class DefaultBackButton extends BackButton {
        private final Menu menu;
        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (menu == null || menu.previous == null)
                return;
            menu.previous.open(player);
        }
    }
}
