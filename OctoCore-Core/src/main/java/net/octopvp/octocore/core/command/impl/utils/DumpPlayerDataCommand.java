package net.octopvp.octocore.core.command.impl.utils;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.errorhandling.Hastebin;
import org.bukkit.command.CommandSender;

public class DumpPlayerDataCommand {
    @Command(name = "dumpplayerdata")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(CommandSender sender, PlayerData target) {
        if (target == null) {
            return CommandResult.INVALID_PLAYER;
        }
        sender.sendMessage(CC.GRAY + "Dumping data, please wait...");
        String data = target.getData().toJson();
        new Hastebin().post(data).thenAcceptAsync((s) -> sender.sendMessage(CC.AQUA + "Pdata dump for: " + target.getName() + "\n" + s));
        return CommandResult.SUCCESS;
    }
}
