package net.octopvp.octocore.paper.module.impl.punishments.menus.buttons;

import lombok.AllArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class DataInfoButton extends Button {
    private PlayerData playerData;
    private int slot;

    @Override
    public ItemStack getItem(Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerData.getUniqueId());

        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
        item.setDurability(3);
        item.setName(CC.MAIN + "Player Info");
        item.addLoreLine("");
        item.addLoreLine(CC.MAIN + "Name&7: " + CC.SECONDARY + playerData.getName());
        item.addLoreLine(CC.MAIN + "UUID&7: " + CC.SECONDARY + playerData.getUniqueId().toString());

        Player onlinePlayer = Bukkit.getPlayer(playerData.getName());
        if (onlinePlayer != null) {
            item.addLoreLine(CC.MAIN + "Last seen&7: " + CC.SECONDARY + "&aNow");
        } else {
            if (playerData.getLastSeen() > 0) {
                item.addLoreLine(CC.MAIN + "Last seen&7: " + CC.SECONDARY + playerData.getLastSeen());
            } else {
                item.addLoreLine(CC.MAIN + "Last seen&7: " + CC.SECONDARY + "&cNever played before!");
            }
        }
        item.addLoreLine(CC.MAIN + "First Joined&7: " + CC.SECONDARY + (offlinePlayer.getFirstPlayed() != 0 ? DateUtils.getDate(offlinePlayer.getFirstPlayed()) : "&cNever played before!"));
        item.addLoreLine("");

        return item.toSkullBuilder().withOwner(playerData.getUniqueId()).buildSkull();
    }

    @Override
    public int getSlot() {
        return this.slot;
    }
}
