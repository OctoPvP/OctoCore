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
public class UnNickCommand {
    @Command(name = "unnick", description = "unnick")
    @Permission(Permissions.COMMAND_UNNICK)
    @PlayerOnly
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0) {
            if (sender.getDisplayName() == sender.getName()) {
                sender.sendMessage(Lang.NOT_NICKED);
                return CommandResult.OTHER;
            }
            sender.getPlayer().setDisplayName(sender.getName());
            NickManager.removeNick(PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId()));
            NameTagChanger.INSTANCE.resetPlayerName(sender.getPlayer());
        } else if (args.length == 1) {
            Player target = null;
            try {
                target = Bukkit.getPlayer(args[1]);
            } catch (Exception e) {
                return CommandResult.PLAYER_NOT_FOUND;
            }
            if (target.getDisplayName() == target.getName()) {
                sender.sendMessage(Lang.OTHER_NOT_NICKED.getMsg(target.getName()));
                return CommandResult.OTHER;
            } else {
                target.setDisplayName(target.getName());
                NameTagChanger.INSTANCE.resetPlayerName(target);
                NickManager.removeNick(PlayerManager.getInstance().getData(target.getUniqueId()));
                target.sendMessage(Lang.NICK_RESET.getMsg());
                sender.sendMessage(Lang.UNNICK_SUCCESS.getMsg(target.getName()));
                return CommandResult.SUCCESS;
            }
        }
        return CommandResult.INVALID_ARGS;
    }
}
