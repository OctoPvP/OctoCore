package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand {
    @Command(name = "ping", description = "pong!")
    public CommandResult execute(CommandSender sender, Player player) {
        if (player != null) {
            if (sender.hasPermission(Permissions.PING_COMMAND_OTHER)) {
                sender.sendMessage(Lang.PING_COMMAND_OTHER_PING.getMsg(player.getName(), player.getPing()));
                return CommandResult.SUCCESS;
            }
        } else {
            if (!(sender instanceof Player)) {
                return CommandResult.PLAYER_ONLY;
            }
            sender.sendMessage(CC.GREEN + "Pong!");
            Player p = (Player) sender;
            int ping = p.getPing();
            sender.sendMessage(Lang.PING_COMMAND_RESPONSE.getMsg());
            if (ping == 56) {
                sender.sendMessage(CC.GREEN + "Your ping is 56ms! You should check out swavy (ign: 56ms) out! https://badbird5907.xyz/swavy");
            }
            return CommandResult.SUCCESS;
        }
        return CommandResult.SUCCESS;
    }


}
