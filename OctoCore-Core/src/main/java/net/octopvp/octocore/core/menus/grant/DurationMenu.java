package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import dev.octomc.agile.menu.Menu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.GrantProcedure;
import net.octopvp.octocore.core.objects.GrantProcedureState;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DurationMenu extends Menu<Gui> {
    private final PlayerData targetData;

    public GuiItem customDurationButton() {
        return ItemBuilder.from(XMaterial.WRITABLE_BOOK.parseMaterial() != null ? XMaterial.WRITABLE_BOOK.parseMaterial() : Material.BOOK)
                .name(Component.text("Custom Duration").color(NamedTextColor.AQUA))
                .lore(Component.text("Click to set a custom duration.").color(NamedTextColor.GREEN))
                .asGuiItem(event -> {
                    callback((Player) event.getWhoClicked());
                });
    }

    public GuiItem permanentButton() {
        return ItemBuilder.from(Material.BEDROCK)
                .name(Component.text("Permanent").color(NamedTextColor.AQUA))
                .lore(Component.text("This will make the duration ").color(NamedTextColor.GREEN).append(Component.text("Permanent").decorate(TextDecoration.UNDERLINED)))
                .asGuiItem(event -> {
                    PlayerData playerData = PlayerManager.getInstance().getData(event.getWhoClicked().getUniqueId());
                    if (playerData == null) {
                        event.getWhoClicked().closeInventory();
                        return;
                    }
                    playerData.getGrantProcedure().setEnteredDuration(-1L);
                    playerData.getGrantProcedure().setPermanent(true);
                    playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
                    event.getWhoClicked().sendMessage(Lang.GRANT_DURATION_SET.getMsg("Permanent"));
                    new GrantReasonMenu(targetData).open((Player) event.getWhoClicked());
                });
    }

    private void callback(Player player) {
        PlayerData playerData = PlayerManager.getInstance().getData(player);
        player.closeInventory();
        SoundUtil.playPing(player);
        OctoCore.getConversationFactory().withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Lang.GRANT_ENTER_DURATION.getMsg();
            }

            @Override
            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                if (playerData == null) {
                    Logger.debug("Player data is null");
                    return Prompt.END_OF_CONVERSATION;
                }
                if (playerData.getGrantProcedure() == null)
                    playerData.setGrantProcedure(new GrantProcedure(playerData));
                if (s.equalsIgnoreCase("perm") || s.equalsIgnoreCase("permanent")) {
                    playerData.getGrantProcedure().setEnteredDuration(-1L);
                    playerData.getGrantProcedure().setPermanent(true);
                    playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
                    player.sendMessage(Lang.GRANT_DURATION_SET.getMsg("Permanent"));
                    new GrantReasonMenu(targetData).open(player);
                    Logger.debug("Player entered permanent");
                    return Prompt.END_OF_CONVERSATION;
                }
                long duration;
                try {
                    duration = System.currentTimeMillis() - DateUtils.parseDateDiff(s, false);
                } catch (Exception e) {
                    player.sendMessage(Lang.GRANT_INVALID_TIME.getMsg());
                    callback(player); //FIXME might not work
                    Logger.debug("Player entered invalid time, restarting conversation");
                    return Prompt.END_OF_CONVERSATION;
                }
                playerData.getGrantProcedure().setPermanent(false);
                playerData.getGrantProcedure().setEnteredDuration(duration);
                player.sendMessage(Lang.GRANT_DURATION_SET.getMsg(playerData.getGrantProcedure().getNiceDuration()));
                playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
                new GrantReasonMenu(targetData).open(player);
                Logger.debug("Player entered valid time");
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false).buildConversation(player).begin();
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title("Choose duration")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(11, customDurationButton());
        gui.setItem(15, permanentButton());
        gui.getFiller().fill(PLACEHOLDER_ITEM);
    }
}
