package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import net.octopvp.commander.annotation.JoinStrings;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.database.redis.packets.server.GlobalCommandPacket;
import net.octopvp.octocore.core.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.core.utils.Sender;
import net.octopvp.octocore.core.utils.msg.Lang;

public class ExecuteOnAllCommand {
    @Command(name = "executeonall", aliases = {"globalexecute"}, usage = "<command>")
    @Permission(Permissions.EXECUTE_ON_ALL_SERVERS)
    @Cooldown(3)
    public CommandResult execute(Sender sender, @JoinStrings String command) {
        new GlobalCommandPacket(command).send();
        new AdminAlertPacket(Lang.ADMIN_ALERT_GLOBAL_EXECUTE.getMsg(sender.getName(), command)).send();
        return CommandResult.SUCCESS;
    }
}
