package net.octopvp.octocore.paper.conversations.grant;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.menus.grant.GrantConfirmationMenu;
import net.octopvp.octocore.paper.objects.GrantProcedureState;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.msg.Lang;
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
        Logger.debug("1");
        player.closeInventory();
        return Lang.GRANT_ENTER_REASON.toString();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        Logger.debug(s);
        playerData.getGrantProcedure().setEnteredReason(s);
        player.sendMessage(Lang.GRANT_REASON_SET.getMsg(s));
        playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.CONFIRMATION);
        new GrantConfirmationMenu().open(player);
        return Prompt.END_OF_CONVERSATION;
    }
}
