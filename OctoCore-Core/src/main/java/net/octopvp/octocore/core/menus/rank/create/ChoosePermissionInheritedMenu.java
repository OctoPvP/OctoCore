package net.octopvp.octocore.core.menus.rank.create;

import dev.octomc.agile.menu.Menu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ChoosePermissionInheritedMenu extends Menu<Gui> {
    private final Consumer<RankBuilder> callback;
    private final Menu<?> instance = this;
    private RankBuilder rankBuilder;

    public ChoosePermissionInheritedMenu(RankBuilder rankBuilder, Consumer<RankBuilder> callback) {
        this.rankBuilder = rankBuilder;
        this.callback = callback;
    }

    public GuiItem doneButton() {
        return ItemBuilder.from(Material.EMERALD_BLOCK)
                //.name(CC.GREEN + "Done!")
                .name(Component.text("Done!").color(NamedTextColor.GREEN))
                .asGuiItem(event -> {
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    callback.accept(rankBuilder);
                });
    }

    public GuiItem inheritedButton() {
        return ItemBuilder.from(Material.ANVIL)
                //.name(CC.AQUA + "Inherited")
                .name(Component.text("Inherited").color(NamedTextColor.AQUA))
                //.lore(CC.SEPARATOR, CC.AQUA + "Inherited Ranks: " + CC.YELLOW + rankBuilder.getRank().getInheritedRanks().size(), CC.SEPARATOR)
                .lore(Component.text(""), Component.text("-----"), Component.text(rankBuilder.getRank().getInheritedRanks().size() + " inherited ranks."), Component.text("-----"))
                .asGuiItem(event -> {
                    new InheritedRanksMenu(instance, rankBuilder, (b) -> {
                        rankBuilder = b;
                        open((Player) event.getWhoClicked());
                        SoundUtil.playPing((Player) event.getWhoClicked());
                    }).open((Player) event.getWhoClicked());
                    SoundUtil.playPing((Player) event.getWhoClicked());
                });
    }

    public GuiItem permissionsButton() {
        return ItemBuilder.from(Material.EMERALD)
                //.name(CC.AQUA + "Permissions")
                .name(Component.text("Permissions").color(NamedTextColor.AQUA))
                //.lore(CC.SEPARATOR, CC.AQUA + "Total Permissions: " + CC.YELLOW + rankBuilder.getRank().getNodes().size(), CC.AQUA + "Total Allowed Permissions: " + CC.YELLOW + rankBuilder.getRank().getAllowedPermissions().size(), CC.AQUA + "Total Negated Permissions: " + CC.YELLOW + rankBuilder.getRank().getNegatedPermissions().size(), CC.SEPARATOR)
                .lore(
                        Component.text(CC.SEPARATOR),
                        Component.text("Total Permissions: ").color(NamedTextColor.AQUA)
                                .append(Component.text(rankBuilder.getRank().getNodes().size()).color(NamedTextColor.YELLOW)),
                        Component.text("Total Allowed Permissions: ").color(NamedTextColor.AQUA)
                                .append(Component.text(rankBuilder.getRank().getAllowedPermissions().size()).color(NamedTextColor.YELLOW)),
                        Component.text("Total Negated Permissions: ").color(NamedTextColor.AQUA)
                                .append(Component.text(rankBuilder.getRank().getNegatedPermissions().size()).color(NamedTextColor.YELLOW)),
                        Component.text(CC.SEPARATOR)
                )
                .asGuiItem(event -> {
                    new CreateRankManagePermissionsMenu(instance, rankBuilder, (b) -> {
                        rankBuilder = b;
                        open((Player) event.getWhoClicked());
                        SoundUtil.playPing((Player) event.getWhoClicked());
                    }).open((Player) event.getWhoClicked());
                    SoundUtil.playPing((Player) event.getWhoClicked());
                });
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title("Choose an action.")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.getFiller().fill(PLACEHOLDER_ITEM);
        gui.setItem(12, permissionsButton());
        gui.setItem(14, inheritedButton());
        gui.setItem(26, doneButton());
        GuiItem backButton = ItemBuilder.from(Material.ARROW)
                //.name(CC.YELLOW + "Back")
                .name(Component.text("Back").color(NamedTextColor.YELLOW))
                .asGuiItem(event -> {
                    callback.accept(rankBuilder);
                    SoundUtil.playPing((Player) event.getWhoClicked());
                });
        gui.setItem(22, backButton);
    }
}
