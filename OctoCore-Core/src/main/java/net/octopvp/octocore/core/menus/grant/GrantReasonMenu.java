package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import dev.octomc.agile.menu.PaginatedMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.util.Logger;
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
                    if (sendData == null || playerData == null) {
                        Logger.debug("Player data is null, cannot continue");
                        player.closeInventory();
                        return;
                    }
                    sendData.getGrantProcedure().setEnteredReason(grantReason.getReason());
                    player.sendMessage(Lang.GRANT_REASON_SET.getMsg(grantReason.getReason()));
                    sendData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.CONFIRMATION);
                    new GrantConfirmationMenu(playerData).open(player);
                });
    }

    public GuiItem otherReason() {
        return ItemBuilder.from(XMaterial.WRITABLE_BOOK.parseMaterial() != null ? XMaterial.WRITABLE_BOOK.parseMaterial() : Material.BOOK)
                .name(Component.text("Other").color(NamedTextColor.AQUA))
                .lore(Component.text("Click to enter a custom reason.").color(NamedTextColor.GREEN))
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    prompt(player);
                    player.closeInventory();
                });
    }

    public void prompt(Player player) {
        OctoCore.getConversationFactory().withFirstPrompt(new GrantReasonConversation(PlayerManager.getInstance().getData(player), playerData, player)).withLocalEcho(false).buildConversation(player).begin();
    }
}
