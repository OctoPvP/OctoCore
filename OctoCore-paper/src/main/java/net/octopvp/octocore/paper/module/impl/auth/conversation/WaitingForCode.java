package net.octopvp.octocore.paper.module.impl.auth.conversation;

import net.octopvp.octocore.paper.module.impl.auth.AuthModule;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class WaitingForCode extends StringPrompt {
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Lang.AUTH_WAITING.toString();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        if(AuthModule.handle2FARequest((Player) conversationContext.getForWhom(),s)){
            return Prompt.END_OF_CONVERSATION;
        }
        return this;
    }
}
