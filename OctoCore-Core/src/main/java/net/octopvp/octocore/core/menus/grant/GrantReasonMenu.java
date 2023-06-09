package net.octopvp.octocore.core.menus.grant;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.agile.util.XMaterial;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.conversations.grant.GrantReasonConversation;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.GrantProcedureState;
import net.octopvp.octocore.core.objects.GrantReason;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GrantReasonMenu extends PaginatedMenu<PaginatedGui> {
    private final PlayerData playerData;

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        for (GrantReason value : GrantReason.values()) {
            items.add(reasonButton(value));
        }
        return items;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Grant Reason")
                .rows(6)
                .create();
    }

    @Override
    public void addStaticButtons() {
        gui.setItem(0, otherReason());
    }

    public GuiItem reasonButton(GrantReason grantReason) {
        return ItemBuilder.from(grantReason.getMaterial())
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    PlayerData sendData = PlayerManager.getInstance().getData(player.getUniqueId());
                    if (sendData == null || !playerData.isOnlineThisServer()) {
                        player.closeInventory();
                        return;
                    }
                    sendData.getGrantProcedure().setEnteredReason(grantReason.getReason());
                    player.sendMessage(Lang.GRANT_REASON_SET.getMsg(grantReason.getReason()));
                    sendData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.CONFIRMATION);
                    new GrantConfirmationMenu().open(player);
                });
    }

    public GuiItem otherReason() {
        return ItemBuilder.from(XMaterial.WRITABLE_BOOK.parseMaterial() != null ? XMaterial.WRITABLE_BOOK.parseMaterial() : Material.BOOK)
                .name(CC.GREEN + "Other")
                .lore(CC.AQUA + "Click to enter a custom reason.")
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    prompt(player);
                    player.closeInventory();
                });
    }

    public void prompt(Player player) {
        OctoCore.getConversationFactory().withFirstPrompt(new GrantReasonConversation(playerData, player)).withLocalEcho(false).buildConversation(player).begin();
    }
}
