package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.Bukkit;

public class ConsoleCommand {
    @Command(name = "console", cooldown = 3, permission = Permission.CONSOLE_EXECUTE, usage = "<command>")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0) {
            return CommandResult.INVALID_ARGS;
        }
        String command = StringUtils.buildString(args, 0);
        if (command.startsWith("/"))
            command.substring(1);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        return CommandResult.SUCCESS;
    }
}
