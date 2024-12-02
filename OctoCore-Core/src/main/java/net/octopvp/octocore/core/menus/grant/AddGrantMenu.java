package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import dev.octomc.agile.menu.PaginatedMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.objects.GrantProcedure;
import net.octopvp.octocore.core.objects.GrantProcedureState;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Buttons;
import net.octopvp.octocore.core.utils.item.WoolUtils;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AddGrantMenu extends PaginatedMenu<PaginatedGui> {
    private final PlayerData data;

    public AddGrantMenu(PlayerData data) {
        this.data = data;
    }

    @Override
    public void addStaticButtons() {
        gui.setItem(4, Buttons.playerInfo(data));
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        RankManager.getRanks().stream().sorted((o1, o2) -> Integer.compare(o2.getWeight(), o1.getWeight())).forEach(rank -> items.add(rankButton(rank, data)));
        return items;
    }

    public GuiItem rankButton(Rank rankData, PlayerData playerData) {
        return ItemBuilder.from(((rankData.isDefaultRank()) ? XMaterial.LIME_WOOL : WoolUtils.convertChatColorToWoolMaterial(rankData.getColor())).parseItem()) // ItemBuilder.from(Material.WOOL)
                // .durability((rankData.isDefaultRank() ? 4 : WoolUtils.convertChatColorToWoolData(rankData.getColor())))
                .name(Component.text(rankData.getDisplayName()))
                .lore(
                        Component.text(CC.SEPARATOR),
                        Component.text("Weight: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(rankData.getWeight()).color(NamedTextColor.YELLOW)),
                        Component.text("Inherited: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(StringUtils.join(rankData.getInheritedRanksName(), ", ")).color(NamedTextColor.YELLOW)),
                        Component.text("Default: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(rankData.isDefaultRank()).color(NamedTextColor.YELLOW)),
                        Component.text("Prefix: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(rankData.getPrefix()).color(NamedTextColor.YELLOW)),
                        Component.text("Changeable Color: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(rankData.isChangableMainColor()).color(NamedTextColor.YELLOW)),
                        Component.text("Purchasable: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(rankData.isPurchasable()).color(NamedTextColor.YELLOW)),
                        Component.text(CC.SEPARATOR)
                )
                .asGuiItem(event -> {
                    if (rankData.isDefaultRank()) {
                        event.getWhoClicked().sendMessage(Lang.GRANT_CANT_GRANT_DEFAULT.getMsg());
                        return;
                    }
                    if (playerData.hasRank(rankData)) {
                        event.getWhoClicked().sendMessage(Lang.GRANT_ALREADY_HAS_RANK.getMsg(playerData.getName(), rankData.getName()));
                        return;
                    }
                    PlayerData pData = PlayerManager.getInstance().getData(event.getWhoClicked().getUniqueId());
                    if (!RankManager.getInstance().canGrant(pData, rankData) && !event.getWhoClicked().hasPermission(Permissions.GRANT_ALL)) {
                        event.getWhoClicked().sendMessage(Lang.GRANT_CANNOT_GRANT_HIGHER_RANK.getMsg());
                        return;
                    }

                    pData.setGrantProcedure(new GrantProcedure(playerData));
                    pData.getGrantProcedure().setRankName(rankData.getName());
                    pData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.SERVER_CHOOSE);

                    new GrantServerMenu(data, this).open((Player) event.getWhoClicked());
                });
    }

    @Override
    public PaginatedGui createGui(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title("Select a rank!")
                .rows(6)
                .create();
        gui.setCloseGuiAction(event -> {
            PlayerData playerData = PlayerManager.getInstance().getData(event.getPlayer().getUniqueId());
            if (playerData.getGrantProcedure() != null && playerData.getGrantProcedure().getGrantProcedureState() == GrantProcedureState.START) {
                playerData.setGrantProcedure(null);
            }
        });

        return gui;

    }
}
