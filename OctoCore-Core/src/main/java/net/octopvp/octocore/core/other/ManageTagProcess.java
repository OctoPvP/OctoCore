package net.octopvp.octocore.core.other;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.conversations.tag.SetDescConversation;
import net.octopvp.octocore.core.conversations.tag.SetMaterialConversation;
import net.octopvp.octocore.core.conversations.tag.SetNameConversation;
import net.octopvp.octocore.core.conversations.tag.SetTagConversation;
import net.octopvp.octocore.core.manager.impl.TagManager;
import net.octopvp.octocore.core.objects.PlayerTagBuilder;
import net.octopvp.octocore.core.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ManageTagProcess {
    private final PlayerTagBuilder builder;
    private final Player player;
    private final boolean edit;

    public ManageTagProcess(Player player) {
        this.player = player;
        this.builder = new PlayerTagBuilder();
        edit = false;
    }

    public ManageTagProcess(PlayerTagBuilder builder, Player player) {
        this.builder = builder;
        this.player = player;
        edit = true;
    }

    public void setNameProcess(ReturnableTypeCallback<PlayerTagBuilder> done) {
        if (builder == null)
            return;
        OctoCore.getConversationFactory().withFirstPrompt(new SetNameConversation((s) -> {
            if (!s.equalsIgnoreCase("cancel"))
                builder.setTagName(s);
            done.call(builder);
        })).withLocalEcho(false).buildConversation(player).begin();
    }

    public void setDescProcess(ReturnableTypeCallback<PlayerTagBuilder> done) {
        if (builder == null)
            return;
        OctoCore.getConversationFactory().withFirstPrompt(new SetDescConversation((s) -> {
            if (!s.equalsIgnoreCase("cancel"))
                builder.setTagDesc(s);
            done.call(builder);
        })).withLocalEcho(false).buildConversation(player).begin();
    }

    public void setTagProcess(ReturnableTypeCallback<PlayerTagBuilder> done) {
        if (builder == null)
            return;
        OctoCore.getConversationFactory().withFirstPrompt(new SetTagConversation((s) -> {
            if (!s.equalsIgnoreCase("cancel"))
                builder.setTag(s);
            done.call(builder);
        })).withLocalEcho(false).buildConversation(player).begin();
    }

    public void setMaterial(ReturnableTypeCallback<PlayerTagBuilder> done) {
        if (builder == null)
            return;
        OctoCore.getConversationFactory().withFirstPrompt(new SetMaterialConversation((s) -> {
            if (!s.equalsIgnoreCase("cancel"))
                builder.setMaterial(Material.matchMaterial(s)); //TODO use gui
            done.call(builder);
        })).withLocalEcho(false).buildConversation(player).begin();
    }

    public void build() {
        if (builder == null)
            return;
        SoundUtil.playPing(player);
        if (edit) {
            builder.build().save();
            player.sendMessage(CC.GREEN + "Tag saved!");
        } else {
            TagManager.createTag(builder.build());
            player.sendMessage(CC.GREEN + "Created tag " + builder.getTagName());
        }
    }

    public PlayerTagBuilder getBuilder() {
        return builder;
    }
}
