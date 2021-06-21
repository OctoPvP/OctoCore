package net.octopvp.octocore.paper.conversations.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.callback.ReturnableTypeCallback;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class SetNameConversation extends StringPrompt {
    private ReturnableTypeCallback<String> callback;
    public SetNameConversation(ReturnableTypeCallback<String> callback){
        this.callback = callback;
    }
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return CC.GREEN + "Please enter the name of the tag in chat. This is " + CC.U + "NOT " + CC.R + CC.GREEN + "the tag seen by players. Just an identifier";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        callback.call(s);
        return END_OF_CONVERSATION;
    }
}
