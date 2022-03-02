package net.octopvp.octocore.paper.command.impl.essentials;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.paper.database.redis.packets.server.GlobalCommandPacket;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.common.object.Permission;

public class ExecuteOnAllCommand extends BaseCommand {
    @Command(name = "executeonall",permission = Permission.EXECUTE_ON_ALL_SERVERS,cooldown = 3,aliases = {"globalexecute"},usage = "<command>")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0){
            return CommandResult.INVALID_ARGS;
        }
        String command = StringUtils.buildString(args,0);
        new GlobalCommandPacket(command).send();
        new AdminAlertPacket(Lang.ADMIN_ALERT_GLOBAL_EXECUTE.getMsg(sender.getName(),command)).send();
        return CommandResult.SUCCESS;
    }
}
