package net.octopvp.octocore.paper.utils.menu.buttons.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerInfoButton extends Button {
    private PlayerData playerData;
    private final int slot;

    public PlayerInfoButton(PlayerData playerData, int slot) {
        this.playerData = playerData;
        this.slot = slot;
    }

    public PlayerInfoButton(UUID uuid, int slot) {
        this.slot = slot;
        playerData = null;
        PlayerManager.getOfflineData(uuid).thenAcceptAsync((data) -> {
            this.playerData = data;
        });
    }

    public PlayerInfoButton(String name, int slot) {
        this.slot = slot;
        playerData = null;
        PlayerManager.getOfflineData(name).thenAcceptAsync((data) -> {
            this.playerData = data;
        });
    }

    @Override
    public ItemStack getItem(Player player) {
        if (playerData == null) {
            ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM);
            builder.setName(CC.GRAY + "Loading...");
            return builder.build();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerData.getUuid());
        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
        item.durability(3);
        item.name(CC.AQUA + playerData.getName());
        item.lore(CC.SEPARATOR,
                CC.AQUA + "Name&7: &b" + playerData.getName(),
                CC.AQUA + "UUID&7: &b" + playerData.getUuid(),
                CC.AQUA + "Rank&7: &b" + playerData.getHighestRank().getDisplayColor() + playerData.getHighestRank().getName(),
                CC.AQUA + "Current Tag&7: &b" + playerData.getTagString());
        Player onlinePlayer = Bukkit.getPlayer(playerData.getName());
        if (onlinePlayer != null) {
            item.addLoreLine(CC.AQUA + "Last seen&7: &aNow");
        } else {
            if (playerData.getLastSeen() != null) {
                item.addLoreLine(CC.AQUA + "Last seen&7: &b" + playerData.getLastSeen());
            } else {
                item.addLoreLine(CC.AQUA + "Last seen&7: &cNever played before!");
            }
        }
        item.addLoreLine(CC.AQUA + "First Joined&7: &b" + (offlinePlayer.getFirstPlayed() != 0 ? DateUtils.getDate(offlinePlayer.getFirstPlayed()) : "&cNever played before!"));
        item.addLoreLine(CC.SEPARATOR);
        return item.toSkullBuilder().withOwner(playerData.getName()).withOwner(playerData.getUuid()).buildSkull();
    }

    @Override
    public int getSlot() {
        return slot;
    }
}
