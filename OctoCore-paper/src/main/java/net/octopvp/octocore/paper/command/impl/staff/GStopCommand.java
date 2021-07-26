package net.octopvp.octocore.paper.command.impl.staff;

import com.google.gson.JsonObject;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.util.json.JsonChain;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.common.object.Permission;

public class GStopCommand extends BaseCommand {
    @Command(name = "gstop",permission = Permission.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {
        String command = "stop";
        JsonObject jsonObject = new JsonChain().addProperty("command",command).get();
        JsonObject adminAlert = new JsonChain().addProperty("message", Lang.ADMIN_ALERT_GLOBAL_EXECUTE.getMsg(sender.getName(),command)).get();
        OctoCore.getInstance().getRedisData().write(JedisAction.GLOBAL_COMMAND,jsonObject);
        OctoCore.getInstance().getRedisData().write(JedisAction.ADMIN_ALERT,adminAlert);
        return CommandResult.SUCCESS;
    }
}
