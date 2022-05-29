package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand {
    @Command(name = "ping", description = "pong!")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission(Permissions.PING_COMMAND_OTHER)) {
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    return CommandResult.PLAYER_NOT_FOUND;
                }
                sender.sendMessage(Lang.PING_COMMAND_OTHER_PING.getMsg(p.getName(), p.getPing()));
                return CommandResult.SUCCESS;
            }
        } else {
            if (!(sender.getCommandSender() instanceof Player)) {
                return CommandResult.PLAYER_ONLY;
            }
            sender.sendMessage(CC.GREEN + "Pong!");
            sender.sendMessage(Lang.PING_COMMAND_RESPONSE.getMsg(sender.getPlayer().getPing()));
            if (sender.getPlayer().getPing() == 56) {
                sender.sendMessage(CC.GREEN + "Your ping is 56ms! You should check out swavy (ign: 56ms) out! https://badbird5907.xyz/swavy");
            }
            return CommandResult.SUCCESS;
        }
        return CommandResult.SUCCESS;
    }


}
