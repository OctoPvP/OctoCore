package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.redis.packets.GlobalCommandPacket;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.command.CommandSender;

public class GStopCommand {
    @Command(name = "gstop")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(CommandSender sender) {
        String command = "stop";
        new GlobalCommandPacket(command).send();
        new AdminAlertPacket(Lang.ADMIN_ALERT_GLOBAL_EXECUTE.getMsg(sender.getName(), command)).send();
        return CommandResult.SUCCESS;
    }
}
