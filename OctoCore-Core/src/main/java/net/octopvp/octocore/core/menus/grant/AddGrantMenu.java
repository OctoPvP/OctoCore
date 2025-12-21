package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import dev.octomc.agile.builder.item.ItemBuilder;
import dev.octomc.agile.guis.Gui;
import dev.octomc.agile.guis.GuiItem;
import dev.octomc.agile.guis.PaginatedGui;
import dev.octomc.agile.menu.PaginatedMenu;
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
        return ItemBuilder.from((rankData.isDefaultRank()) ? XMaterial.LIME_WOOL : WoolUtils.convertChatColorToWoolMaterial(rankData.getColor())) // ItemBuilder.from(Material.WOOL)
                // .durability((rankData.isDefaultRank() ? 4 : WoolUtils.convertChatColorToWoolData(rankData.getColor())))
                .name(rankData.getDisplayName())
                .lore(CC.SEPARATOR, CC.AQUA + "Weight" + CC.GRAY + ": " + CC.YELLOW + rankData.getWeight(), CC.AQUA + "Inherited: " + CC.YELLOW + StringUtils.join(rankData.getInheritedRanksName(), ", "), CC.AQUA + "Default: " + CC.YELLOW + rankData.isDefaultRank(),
                        CC.AQUA + "Prefix: " + CC.YELLOW + rankData.getPrefix(), CC.AQUA + "Changeable Color: " + CC.YELLOW + rankData.isChangableMainColor(), CC.AQUA + "Purchasable: " + CC.YELLOW + rankData.isPurchasable(),
                        CC.SEPARATOR
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
