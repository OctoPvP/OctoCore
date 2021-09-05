package net.octopvp.octocore.paper.conversations.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class SetDescConversation extends StringPrompt {
    private ReturnableTypeCallback<String> callback;
    public SetDescConversation(ReturnableTypeCallback<String> callback){
        this.callback = callback;
    }
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return CC.GREEN + "Please enter description";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        callback.call(s);
        return END_OF_CONVERSATION;
    }
}
