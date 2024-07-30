package net.octopvp.octocore.core.menus;

import dev.octomc.agile.menu.PaginatedMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                .name(Component.text(data.getFormattedName(false, player, true)))
                /*.lore(
                        CC.GREEN + "Rank" + CC.GRAY + ": " + data.getHighestRank().getDisplayName(),
                        CC.GREEN + "Vanished" + CC.GRAY + ": " + data.isVanished(),
                        CC.GREEN + "Nicked" + CC.GRAY + ": " + (data.isNicked() ? CC.GREEN + "Yes" : CC.RED + "No"),
                        CC.GREEN + "First Join" + CC.GRAY + ": " + new Date(data.getFirstJoin())
                )//*/
                .lore(
                        Component.text("Rank").color(NamedTextColor.GREEN)
                                .append(Component.text(": ").color(NamedTextColor.GRAY))
                                .append(Component.text(data.getHighestRank().getDisplayName()).color(NamedTextColor.GRAY)),
                        Component.text("Vanished").color(NamedTextColor.GREEN)
                                .append(Component.text(": ").color(NamedTextColor.GRAY))
                                .append(Component.text(data.isVanished()).color(NamedTextColor.GRAY)),
                        Component.text("Nicked").color(NamedTextColor.GREEN)
                                .append(Component.text(": ").color(NamedTextColor.GRAY))
                                .append(data.isNicked() ? Component.text("Yes").color(NamedTextColor.GREEN) : Component.text("No").color(NamedTextColor.RED)),
                        Component.text("First Join").color(NamedTextColor.GREEN)
                                .append(Component.text(": ").color(NamedTextColor.GRAY))
                                .append(Component.text(new Date(data.getFirstJoin()).toString()).color(NamedTextColor.GRAY))
                )
                .asGuiItem();
    }
}
