package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.NickManager;
import net.octopvp.octocore.paper.manager.PlayerManager;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.nametag.NameTagChanger;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class NickCommand implements BaseCommand {
    @Command(name = "nick", description = "nick",usage = "/nick [name]",permission = Permission.COMMAND_NICK)
    public CommandResult execute(Sender sender, String[] args) {
        if(args.length == 1){
            NameTagChanger.INSTANCE.changePlayerName(sender.getPlayer(), args[0]);
            sender.getPlayer().setDisplayName(args[0]);
            sender.getPlayer().setPlayerListName(args[0]);
            NickManager.addNick(PlayerManager.getProfile(sender.getPlayer().getUniqueId()), args[0]);
            for(Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(sender.getPlayer());
                p.showPlayer(sender.getPlayer());
            }
            sender.sendMessage(Lang.NICK_SUCCESS.getMsg().replaceFirst("%nick%",args[0]));
            return CommandResult.SUCCESS;
        }else if(args.length == 2){
            Player target = null;
            try {
                target = Bukkit.getPlayer(args[1]);
            } catch (Exception e) {
                return CommandResult.PLAYER_NOT_FOUND;
            }
            NameTagChanger.INSTANCE.changePlayerName(target,args[0]);
            target.setDisplayName(args[0]);
            target.setPlayerListName(args[0]);
            NickManager.addNick(PlayerManager.getProfile(target.getUniqueId()), args[0]);
            for(Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(sender.getPlayer());
                p.showPlayer(sender.getPlayer());
            }
            target.sendMessage(Lang.NICK_SUCCESS.getMsg(args[0]));
            sender.sendMessage(Lang.NICK_OTHER_SUCCESS.getMsg(target.getName(),args[0]));
            return CommandResult.SUCCESS;
        }
        return CommandResult.INVALID_ARGS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}
