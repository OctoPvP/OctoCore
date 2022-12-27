package net.octopvp.octocore.core.menus.grant;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
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
    private final PlayerData data;

    public GuiItem customDurationButton() {
        return ItemBuilder.from(Material.BOOK_AND_QUILL)
                .name(CC.AQUA + "Custom Duration")
                .lore(CC.GREEN + "Click to set a custom duration.")
                .asGuiItem(event -> {
                    callback(data, (Player) event.getWhoClicked());
                });
    }

    public GuiItem permanentButton() {
        return ItemBuilder.from(Material.BEDROCK)
                .name(CC.AQUA + "Permanent")
                .lore(CC.GREEN + "This will make the duration " + CC.UNDERLINE + "Permanent")
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
                    new GrantReasonMenu(data).open((Player) event.getWhoClicked());
                });
    }

    private void callback(PlayerData playerData, Player player) {
        player.closeInventory();
        SoundUtil.playPing(player);
        OctoCore.getConversationFactory().withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Lang.GRANT_ENTER_DURATION.getMsg();
            }

            @Override
            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                if (playerData == null || !playerData.isOnlineThisServer())
                    return Prompt.END_OF_CONVERSATION;
                if (playerData.getGrantProcedure() == null)
                    playerData.setGrantProcedure(new GrantProcedure(playerData));
                if (s.equalsIgnoreCase("perm") || s.equalsIgnoreCase("permanent")) {
                    playerData.getGrantProcedure().setEnteredDuration(-1L);
                    playerData.getGrantProcedure().setPermanent(true);
                    playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
                    player.sendMessage(Lang.GRANT_DURATION_SET.getMsg("Permanent"));
                    new GrantReasonMenu(data).open(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                long duration;
                try {
                    duration = System.currentTimeMillis() - DateUtils.parseDateDiff(s, false);
                } catch (Exception e) {
                    player.sendMessage(Lang.GRANT_INVALID_TIME.getMsg());
                    callback(playerData, player); //FIXME might not work
                    return Prompt.END_OF_CONVERSATION;
                }
                playerData.getGrantProcedure().setPermanent(false);
                playerData.getGrantProcedure().setEnteredDuration(duration);
                player.sendMessage(Lang.GRANT_DURATION_SET.getMsg(playerData.getGrantProcedure().getNiceDuration()));
                playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
                new GrantReasonMenu(data).open(player);
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false).buildConversation(player).begin();
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title(CC.AQUA + "Choose duration")
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
