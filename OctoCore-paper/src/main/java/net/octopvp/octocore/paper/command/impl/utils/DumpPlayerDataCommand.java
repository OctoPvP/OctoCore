package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.errorhandling.Hastebin;

public class DumpPlayerDataCommand {
    @Command(name = "dumpplayerdata")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(Sender sender, PlayerData target) {
        if (target == null) {
            return CommandResult.INVALID_PLAYER;
        }
        sender.sendMessage(CC.GRAY + "Dumping data, please wait...");
        String data = target.save(true).toJson();
        new Hastebin().post(data).thenAcceptAsync((s) -> sender.sendMessage(CC.AQUA + "Pdata dump for: " + target.getName() + "\n" + s));
        return CommandResult.SUCCESS;
    }
}
