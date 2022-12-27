package net.octopvp.octocore.core.conversations.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class SetMaterialConversation extends StringPrompt {
    private final ReturnableTypeCallback<String> callback;

    public SetMaterialConversation(ReturnableTypeCallback<String> callback) {
        this.callback = callback;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return CC.GREEN + "Please enter the material you would like the tag to show up as in /tags (Use minecraft namespaced id)";
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        callback.call(s);
        return END_OF_CONVERSATION;
    }
}
