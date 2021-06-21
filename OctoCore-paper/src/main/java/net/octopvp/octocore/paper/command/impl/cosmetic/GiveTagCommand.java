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
import net.octopvp.octocore.paper.menus.tag.GiveTagsMenu;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.objects.enums.DataUpdateReason;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.json.JsonChain;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class GiveTagCommand extends BaseCommand {
    @Command(name = "givetag")
    public CommandResult execute(Sender sender, String[] args) {
        if (!(args.length >= 1)){
            sender.sendMessage(CC.RED + "Usage: /givetag <player> [tag]");
            return CommandResult.INVALID_ARGS;
        }
        String target = args[0];
        String tag = "";
        if (args.length == 2)
            tag = args[1];
        if (OctoCore.getServerManager().isPlayerOnline(target)){
            if (Bukkit.getPlayer(target) != null){
                //on this server
                PlayerData profile = PlayerManager.getProfile(Bukkit.getPlayer(target).getUniqueId());
                if (tag == ""){
                    List<PlayerTag> tags = new ArrayList<>();
                    profile.getAllowedTags().forEach(t ->tags.add(TagManager.getTag(t)));
                    new GiveTagsMenu(tags,target).open(sender.getPlayer());
                    return CommandResult.SUCCESS;
                }
                PlayerTag tag1 = TagManager.getTagByName(tag);
                if (!profile.getAllowedTags().contains(tag1.getId()))
                    profile.getAllowedTags().add(tag1.getId());
                List<PlayerTag> tags = new ArrayList<>();
                profile.getAllowedTags().forEach(t ->tags.add(TagManager.getTag(t)));
                sender.sendMessage(CC.GREEN + "Added tag " + tag1.getName() + " to " + target);
                return CommandResult.SUCCESS;
            }else{
                //on some other network server
                GlobalPlayer gPlayer = OctoCore.getServerManager().getGlobalPlayer(target);
                if (tag == ""){
                    new GiveTagsMenu(gPlayer.getAllTags(), target).open(sender.getPlayer());
                    return CommandResult.SUCCESS;
                }
                JsonObject jsonObject = new JsonChain().addProperty("reason", DataUpdateReason.TAGS_UPDATE_GIVE.name()).addProperty("target",target).addProperty("add",tag).get();
                OctoCore.getInstance().getRedisData().write(JedisAction.PDATA_UPDATE,jsonObject);
                sender.sendMessage(CC.GREEN + "Requested pdata update for " + target + " reason: update owned tags");
                List<PlayerTag> tags = new ArrayList<>();
                gPlayer.getAllTags().forEach(t ->tags.add(TagManager.getTag(t.getId())));
            }
        }else{
            //player is offline
            sender.sendMessage(CC.GREEN + "Attempting to load " + target + "'s playerdata");
            PlayerData data = PlayerManager.getProfileFromDB(target);
            if (data == null)
                return CommandResult.PLAYER_NOT_FOUND;
            sender.sendMessage(CC.GREEN + "Found " + target + "'s data!");
            if (tag == ""){
                List<PlayerTag> tags = new ArrayList<>();
                data.getAllowedTags().forEach(t -> tags.add(TagManager.getTag(t)));
                //new GiveTagsMenu(target,tags).open(sender.getPlayer());
                new GiveTagsMenu(tags,target).open(sender.getPlayer());
                return CommandResult.SUCCESS;
            }
            PlayerTag tag1 = TagManager.getTagByName(tag);
            if (!data.hasTag(tag1.getName()))
                data.getAllowedTags().add(tag1.getId());
            PlayerManager.saveProfile(data);
            sender.sendMessage(CC.GREEN + "Added tag " + tag1.getName() + " to " + target);
            return CommandResult.SUCCESS;
        }
        return CommandResult.SUCCESS;
    }
}