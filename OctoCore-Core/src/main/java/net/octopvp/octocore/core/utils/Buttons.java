package net.octopvp.octocore.core.utils;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.components.util.Legacy;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.ServerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Buttons {
    public static GuiItem playerInfo(PlayerData playerData) {
        if (playerData == null) {
            return ItemBuilder.skull()
                    .name(CC.GRAY + "Loading...")
                    .owner(Bukkit.getOfflinePlayer(UUID.randomUUID()))
                    .asGuiItem();
        }
        // OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerData.getUuid());
        OfflineHelpers.OfflineInfo offlinePlayer = OfflineHelpers.getOfflineInfo(playerData.getUuid());
        List<String> lore = new ArrayList<>(
                Arrays.asList(
                        CC.SEPARATOR,
                        CC.AQUA + "Name" + CC.GRAY + ": " + CC.AQUA + playerData.getName(),
                        CC.AQUA + "UUID" + CC.GRAY + ": " + CC.AQUA + playerData.getUuid(),
                        CC.AQUA + "Rank" + CC.GRAY + ": " + CC.AQUA + playerData.getHighestRank().getDisplayColor() + playerData.getHighestRank().getName(),
                        CC.AQUA + "Current Tag" + CC.GRAY + ": " + CC.AQUA + playerData.getTagString()
                )
        );
        Player onlinePlayer = Bukkit.getPlayer(playerData.getName());
        if (onlinePlayer != null) {
            lore.add(CC.AQUA + "Last seen" + CC.GRAY + ": " + CC.GREEN + "Now (On this server)");
        } else {
            if (OctoCore.getInstance().getServerManager().isOnline(playerData.getUuid())) {
                lore.add(CC.AQUA + "Last seen" + CC.GRAY + ": " + CC.GREEN + "Now (On " + OctoCore.getInstance().getServerManager().getOnlinePlayer(playerData.getUuid()).getServer() + ")");
            } else if (playerData.getLastSeen() > 0) {
                lore.add(CC.AQUA + "Last seen" + CC.GRAY + ": " + CC.AQUA + new Date(playerData.getLastSeen()));
            } else {
                lore.add(CC.AQUA + "Last seen" + CC.GRAY + ": " + CC.RED + "Never played before!");
            }
        }
        lore.add(CC.AQUA + "First Joined" + CC.GRAY + ": " + CC.AQUA + (playerData.getFirstJoin() != 0 ? DateUtils.getDate(playerData.getFirstJoin()) : "Never played before!"));
        lore.add(CC.SEPARATOR);
        return ItemBuilder.skull()
                .name(CC.AQUA + playerData.getName())
                .lore(lore.stream().map(Legacy.SERIALIZER::deserialize).collect(Collectors.toList()))
                .owner(Bukkit.getOfflinePlayer(offlinePlayer.getUniqueId()))
                .asGuiItem();
    }

    @Deprecated
    public static GuiItem playerInfo(String name) {
        return playerInfo(PlayerManager.getInstance().getOfflineData(name));
    }

    public static GuiItem playerInfo(UUID uniqueId) {
        return playerInfo(PlayerManager.getInstance().getOfflineData(uniqueId));
    }
}
