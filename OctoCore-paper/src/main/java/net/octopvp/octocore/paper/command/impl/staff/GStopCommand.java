package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.packets.server.GlobalCommandPacket;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.common.object.Permission;

public class GStopCommand extends BaseCommand {
    @Command(name = "gstop",permission = Permission.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {
        String command = "stop";
        new GlobalCommandPacket(command).send();
        new AdminAlertPacket(Lang.ADMIN_ALERT_GLOBAL_EXECUTE.getMsg(sender.getName(),command)).send();
        return CommandResult.SUCCESS;
    }
}
