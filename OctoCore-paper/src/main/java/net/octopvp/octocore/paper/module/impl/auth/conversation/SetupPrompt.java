package net.octopvp.octocore.paper.module.impl.auth.conversation;

import net.octopvp.octocore.paper.module.impl.auth.AuthModule;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class SetupPrompt extends StringPrompt {
    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Lang.AUTH_SETUP_WAITING.toString();
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        Player p = (Player) conversationContext.getForWhom();
        if(s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("exit") || s.equalsIgnoreCase("abort")){
            p.sendMessage(Lang.AUTH_SETUP_ABORTED.toString());
            return Prompt.END_OF_CONVERSATION;
        }
        if(AuthModule.handle2FARequest(p,s))
            return Prompt.END_OF_CONVERSATION;
        return this;
    }
}
