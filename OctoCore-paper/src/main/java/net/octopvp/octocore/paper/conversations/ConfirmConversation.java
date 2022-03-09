package net.octopvp.octocore.paper.conversations;

import net.octopvp.octocore.common.util.callback.BooleanCallback;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class ConfirmConversation extends StringPrompt {
    private final String action;
    private final BooleanCallback callback;

    public ConfirmConversation(String action, BooleanCallback callback) {
        this.action = action;
        this.callback = callback;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Lang.ARE_YOU_SURE.getMsg(action);
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        if (s.equalsIgnoreCase("yes")) {
            callback.callback(true);
            return Prompt.END_OF_CONVERSATION;
        } else if (s.equalsIgnoreCase("no")) {
            callback.callback(false);
            return Prompt.END_OF_CONVERSATION;
        }
        conversationContext.getForWhom().sendRawMessage(Lang.CONFIRM_CONVERSATION_UNKNOWN_RESPONSE.getMsg());
        return new ConfirmConversation(action, callback);
    }
}
