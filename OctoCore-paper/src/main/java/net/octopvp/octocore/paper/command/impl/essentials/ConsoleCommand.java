package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;
import org.bukkit.Bukkit;

public class ConsoleCommand extends BaseCommand {
    @Command(name = "console",cooldown = 3,permission = Permission.CONSOLE_EXECUTE,usage = "<command>")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0){
            return CommandResult.INVALID_ARGS;
        }
        String command = StringUtils.buildString(args,0);
        if (command.startsWith("/"))
            command.substring(1);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
        return CommandResult.SUCCESS;
    }
}
