package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Disable;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.NickManager;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.nametag.NameTagChanger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Disable
public class NickCommand {
    @Command(name = "nick", description = "nick", usage = "[name]")
    @Permission(Permissions.COMMAND_NICK)
    @PlayerOnly
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 1) {
            NameTagChanger.INSTANCE.changePlayerName(sender.getPlayer(), args[0]);
            sender.getPlayer().setDisplayName(args[0]);
            sender.getPlayer().setPlayerListName(args[0]);
            NickManager.addNick(PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId()), args[0]);
            sender.sendMessage(Lang.NICK_SUCCESS.getMsg().replaceFirst("%nick%", args[0]));
            return CommandResult.SUCCESS;
        } else if (args.length == 2) {
            Player target = null;
            try {
                target = Bukkit.getPlayer(args[1]);
            } catch (Exception e) {
                return CommandResult.PLAYER_NOT_FOUND;
            }
            NameTagChanger.INSTANCE.changePlayerName(target, args[0]);
            target.setDisplayName(args[0]);
            target.setPlayerListName(args[0]);
            NickManager.addNick(PlayerManager.getInstance().getData(target.getUniqueId()), args[0]);
            target.sendMessage(Lang.NICK_SUCCESS.getMsg(args[0]));
            sender.sendMessage(Lang.NICK_OTHER_SUCCESS.getMsg(target.getName(), args[0]));
            return CommandResult.SUCCESS;
        }
        return CommandResult.INVALID_ARGS;
    }


}
