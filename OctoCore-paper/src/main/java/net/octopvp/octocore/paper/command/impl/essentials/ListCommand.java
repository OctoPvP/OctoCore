package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.menus.ListMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.common.object.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCommand extends BaseCommand {
    @Command(name = "list",aliases = {"players"} ,description = "List all online players (gui)",permission = Permission.LIST_PLAYERS)
    public CommandResult execute(Sender sender, String[] args) {
        if(sender.getCommandSender() instanceof Player){
            //do the gui thing
            if(args.length > 1){
                sender.sendMessage(Lang.LIST_MESSAGE_HEADER.getMsg(PlayerManager.getPlayerProfiles().size()));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData playerData = PlayerManager.getProfile(player.getUniqueId());
                    String msg;
                    if(playerData == null)
                        msg = CC.GRAY + player.getName();
                    else msg = playerData.getFormattedName(false,player);
                    sender.sendMessage(Lang.LIST_MESSAGE_BODY_ENTRY.getMsg(msg));
                }
                sender.sendMessage(CC.SEPARATOR);
            }
            else{
                new ListMenu().open(sender.getPlayer());
            }
        } else {
            //console
            sender.sendMessage(Lang.LIST_MESSAGE_HEADER.getMsg(PlayerManager.getPlayerProfiles().size()));
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerData playerData = PlayerManager.getProfile(player.getUniqueId());
                String msg;
                if(playerData == null)
                    msg = CC.GRAY + player.getName();
                else msg = playerData.getFormattedName(false,player);
                sender.sendMessage(Lang.LIST_MESSAGE_BODY_ENTRY.getMsg(msg));
            }
        }
        return null;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}
