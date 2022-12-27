package net.octopvp.octocore.core.menus;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;

public class ListMenu extends PaginatedMenu<PaginatedGui> {

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Online Players")
                .rows(6)
                .create();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        return Bukkit.getOnlinePlayers().stream().map(this::item).collect(java.util.stream.Collectors.toList());
    }

    public GuiItem item(Player player) {
        PlayerData data = PlayerManager.getInstance().getData(player);
        return ItemBuilder.skull()
                .owner(player)
                .setName(data.getDisplayName())
                .lore(
                        CC.GREEN + "Rank" + CC.GRAY + ": " + data.getHighestRank().getDisplayName(),
                        CC.GREEN + "Nicked" + CC.GRAY + ": " + (data.isNicked() ? CC.GREEN + "Yes" : CC.RED + "No"),
                        CC.GREEN + "First Join" + CC.GRAY + ": " + new Date(data.getFirstJoin())
                )
                .asGuiItem();
    }
}
