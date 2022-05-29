package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.Bukkit;

public class ConsoleCommand {
    @Command(name = "console", usage = "<command>")
    @Cooldown(3)
    @Permission(Permissions.CONSOLE_EXECUTE)
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
