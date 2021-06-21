package net.octopvp.octocore.paper.command.impl.cosmetic;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.objects.enums.DataUpdateReason;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.json.JsonChain;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.Bukkit;

public class RemoveTagCommand extends BaseCommand {
    @Command(name = "removetag",permission = Permission.ADMIN,cooldown = 1)
    public CommandResult execute(Sender sender, String[] args) {
        if (!(args.length >= 2)){
            sender.sendMessage(CC.RED + "Usage: /remove <player> [tag]");
            return CommandResult.INVALID_ARGS;
        }
        String target = args[0];
        String tag = args[1];
        if (OctoCore.getServerManager().isPlayerOnline(target)){
            if (Bukkit.getPlayer(target) != null){
                //on this server
                PlayerData profile = PlayerManager.getProfile(Bukkit.getPlayer(target).getUniqueId());
                PlayerTag tag1 = TagManager.getTagByName(tag);
                if (!profile.hasTag(tag1.getName()))
                    profile.getAllowedTags().remove(tag1.getId());
                sender.sendMessage(Lang.TAG_REMOVE_SUCCESS.getMsg(tag1.getName(),profile.getName()));
                return CommandResult.SUCCESS;
            }else{
                //on some other network server
                JsonObject jsonObject = new JsonChain().addProperty("reason", DataUpdateReason.TAGS_UPDATE_REMOVE.name()).addProperty("target",target).addProperty("remove",tag).get();
                OctoCore.getInstance().getRedisData().write(JedisAction.PDATA_UPDATE,jsonObject);
                sender.sendMessage(CC.GREEN + "Requested pdata update for " + target + " reason: update owned tags");
            }
        }else{
            //player is offline
            sender.sendMessage(CC.GREEN + "Attempting to load " + target + "'s playerdata");
            PlayerData data = PlayerManager.getProfileFromDB(target);
            if (data == null)
                return CommandResult.PLAYER_NOT_FOUND;
            sender.sendMessage(CC.GREEN + "Found " + target + "'s data!");
            PlayerTag tag1 = TagManager.getTagByName(tag);
            if (data.getAllowedTags().contains(tag1.getId()))
                data.getAllowedTags().remove(tag1.getId());
            PlayerManager.saveProfile(data);
            sender.sendMessage(CC.GREEN + "Added tag " + tag1.getName() + " to " + target);
            return CommandResult.SUCCESS;
        }
        return CommandResult.SUCCESS;
    }
}
