package net.octopvp.octocore.paper.utils.menu.button;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CloseButton extends Button {
    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.BARRIER).name(CC.RED + "Close").lore(CC.RED + "Click to close this menu").build();
    }

    @Override
    public void click(Player player, ClickType clickType) {
        super.click(player, clickType);
        player.getOpenInventory().close();
    }
}
