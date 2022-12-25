package net.octopvp.octocore.core.conversations.grant;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.menus.grant.GrantConfirmationMenu;
import net.octopvp.octocore.core.objects.GrantProcedureState;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GrantReasonConversation extends StringPrompt {
    private final PlayerData playerData;
    private final Player player;

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        player.closeInventory();
        return Lang.GRANT_ENTER_REASON.toString();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        Logger.debug(s);
        if (playerData == null || playerData.getGrantProcedure() == null) {
            player.sendMessage(CC.RED + "Cancelled.");
            return END_OF_CONVERSATION;
        }
        playerData.getGrantProcedure().setEnteredReason(s);
        player.sendMessage(Lang.GRANT_REASON_SET.getMsg(s));
        playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.CONFIRMATION);
        new GrantConfirmationMenu().open(player);
        return Prompt.END_OF_CONVERSATION;
    }
}
