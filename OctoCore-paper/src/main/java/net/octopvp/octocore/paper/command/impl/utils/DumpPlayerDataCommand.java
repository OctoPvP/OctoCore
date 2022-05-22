package net.octopvp.octocore.paper.command.impl.utils;

import com.google.gson.GsonBuilder;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.errorhandling.Hastebin;

public class DumpPlayerDataCommand {
    @Command(name = "dumpplayerdata", permission = Permission.ADMIN, usage = "<player>")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length != 1)
            return CommandResult.INVALID_ARGS;
        sender.sendMessage(CC.GRAY + "Dumping data, please wait...");
        String data = new GsonBuilder().setPrettyPrinting().create().toJson(PlayerManager.getInstance().getOfflineData(args[0]));
        new Hastebin().post(data).thenAcceptAsync((s) -> sender.sendMessage(CC.AQUA + "Pdata dump for: " + args[0] + "\n" + s));
        return CommandResult.SUCCESS;
    }
}
