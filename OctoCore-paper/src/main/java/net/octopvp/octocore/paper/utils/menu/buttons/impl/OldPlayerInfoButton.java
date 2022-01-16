package net.octopvp.octocore.paper.utils.menu.buttons.impl;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class OldPlayerInfoButton extends Button {
    private final PlayerData playerData;
    private final int slot;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
        item.durability(3);
        item.name(CC.AQUA + playerData.getName());
        item.lore(CC.SEPARATOR,CC.AQUA + "Name: " + playerData.getName(),CC.AQUA + "Current Rank: " + playerData.getHighestRank().getName(),CC.AQUA + "Current Tag: " + playerData.getTagString(),CC.AQUA + "Last Seen: " + playerData.getLastSeenAgo(),CC.SEPARATOR);
        return item.toSkullBuilder().withOwner(playerData.getName()).withOwner(playerData.getUuid()).buildSkull();
    }

    @Override
    public int getSlot() {
        return slot;
    }
}
