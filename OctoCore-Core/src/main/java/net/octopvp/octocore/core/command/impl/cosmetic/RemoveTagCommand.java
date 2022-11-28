package net.octopvp.octocore.core.command.impl.cosmetic;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.database.redis.packets.player.TagUpdatePacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.TagManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.PlayerTag;
import net.octopvp.octocore.core.utils.Sender;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class RemoveTagCommand {
    @Command(name = "removetag", usage = "<player> [tag]")
    @Permission(Permissions.ADMIN)
    @Cooldown(1)
    public CommandResult execute(Sender sender, @Required String target, @Required String tag) {
        if (OctoCore.getInstance().getServerManager().isPlayerOnline(target)) {
            if (Bukkit.getPlayer(target) != null) {
                //on this server
                PlayerData profile = PlayerManager.getInstance().getData(Bukkit.getPlayer(target).getUniqueId());
                PlayerTag tag1 = TagManager.getTagByName(tag);
                if (!profile.hasTag(tag1.getName()))
                    profile.removeTag(tag1.getId());
                sender.sendMessage(Lang.TAG_REMOVE_SUCCESS.getMsg(tag1.getName(), profile.getName()));
                return CommandResult.SUCCESS;
            } else {
                PlayerTag tag1 = TagManager.getTagByName(tag);
                if (tag1 == null) {
                    sender.sendMessage(Lang.TAG_NOT_FOUND.getMsg(tag));
                    return CommandResult.SUCCESS;
                }
                //on some other network server
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);
                new TagUpdatePacket(TagUpdatePacket.TagUpdateReason.REMOVE_TAG, offlinePlayer.getUniqueId(), tag1.getId()).send();
                sender.sendMessage(CC.GREEN + "Requested pdata update for " + target + " reason: update owned tags");
            }
        } else {
            //player is offline
            sender.sendMessage(CC.GREEN + "Attempting to load " + target + "'s playerdata");
            PlayerData data = PlayerManager.getInstance().getOfflineData(target);
            if (data == null)
                return CommandResult.PLAYER_NOT_FOUND;
            sender.sendMessage(CC.GREEN + "Found " + target + "'s data!");
            PlayerTag tag1 = TagManager.getTagByName(tag);
            if (data.hasTag(tag1))
                data.removeTag(tag1.getId());
            data.save();
            sender.sendMessage(CC.GREEN + "Added tag " + tag1.getName() + " to " + target);
            return CommandResult.SUCCESS;
        }
        return CommandResult.SUCCESS;
    }
}
