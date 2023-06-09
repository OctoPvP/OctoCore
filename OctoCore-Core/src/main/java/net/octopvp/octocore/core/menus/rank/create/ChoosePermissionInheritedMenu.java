package net.octopvp.octocore.core.menus.rank.create;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
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
                .name(CC.GREEN + "Done!")
                .asGuiItem(event -> {
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    callback.accept(rankBuilder);
                });
    }

    public GuiItem inheritedButton() {
        return ItemBuilder.from(Material.ANVIL)
                .name(CC.AQUA + "Inherited")
                .lore(CC.SEPARATOR, CC.AQUA + "Inherited Ranks: " + CC.YELLOW + rankBuilder.getRank().getInheritedRanks().size(), CC.SEPARATOR)
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
                .name(CC.AQUA + "Permissions")
                .lore(CC.SEPARATOR, CC.AQUA + "Total Permissions: " + CC.YELLOW + rankBuilder.getRank().getNodes().size(), CC.AQUA + "Total Allowed Permissions: " + CC.YELLOW + rankBuilder.getRank().getAllowedPermissions().size(), CC.AQUA + "Total Negated Permissions: " + CC.YELLOW + rankBuilder.getRank().getNegatedPermissions().size(), CC.SEPARATOR)
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
        gui.setItem(10, permissionsButton());
        gui.setItem(12, inheritedButton());
        gui.setItem(26, doneButton());
        GuiItem backButton = ItemBuilder.from(Material.ARROW)
                .name(CC.YELLOW + "Back")
                .asGuiItem(event -> {
                    callback.accept(rankBuilder);
                    SoundUtil.playPing((Player) event.getWhoClicked());
                });
        gui.setItem(22, backButton);
    }
}
