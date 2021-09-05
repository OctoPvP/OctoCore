package net.octopvp.octocore.paper.other;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.conversations.tag.SetDescConversation;
import net.octopvp.octocore.paper.conversations.tag.SetMaterialConversation;
import net.octopvp.octocore.paper.conversations.tag.SetNameConversation;
import net.octopvp.octocore.paper.conversations.tag.SetTagConversation;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerTagBuilder;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CreateTagProcess {
    private PlayerTagBuilder builder;
    private Player player;
    public CreateTagProcess(Player player){
        this.player = player;
        this.builder = new PlayerTagBuilder();
    }
    public void setNameProcess( ReturnableTypeCallback<PlayerTagBuilder> done){
        if (builder == null)
            return;
        OctoCore.getConversationFactory().withFirstPrompt(new SetNameConversation((s)->{
            if (!s.equalsIgnoreCase("cancel"))
                builder.setTagName(s);
            done.call(builder);
        })).withLocalEcho(false).buildConversation(player).begin();
    }
    public void setDescProcess( ReturnableTypeCallback<PlayerTagBuilder> done){
        if (builder == null)
            return;
        OctoCore.getConversationFactory().withFirstPrompt(new SetDescConversation((s)->{
            if (!s.equalsIgnoreCase("cancel"))
                builder.setTagDesc(s);
            done.call(builder);
        })).withLocalEcho(false).buildConversation(player).begin();
    }
    public void setTagProcess( ReturnableTypeCallback<PlayerTagBuilder> done){
        if (builder == null)
            return;
        OctoCore.getConversationFactory().withFirstPrompt(new SetTagConversation((s)->{
            if (!s.equalsIgnoreCase("cancel"))
                builder.setTag(s);
            done.call(builder);
        })).withLocalEcho(false).buildConversation(player).begin();
    }
    public void setMaterial(ReturnableTypeCallback<PlayerTagBuilder> done){
        if (builder == null)
            return;
        OctoCore.getConversationFactory().withFirstPrompt(new SetMaterialConversation((s)->{
            if (!s.equalsIgnoreCase("cancel"))
                builder.setMaterial(Material.matchMaterial(s)); //TODO use gui
            done.call(builder);
        })).withLocalEcho(false).buildConversation(player).begin();
    }
    public void build(){
        if (builder == null)
            return;
        TagManager.createTag(builder.build());
        player.sendMessage(CC.GREEN + "Created tag " + builder.getTagName());
    }

    public PlayerTagBuilder getBuilder() {
        return builder;
    }
}
