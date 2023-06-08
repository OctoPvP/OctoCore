package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ConsoleCommand {
    @Command(name = "console")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(@Sender CommandSender sender, @JoinStrings String command) {
        if (command.startsWith("/"))
            command.substring(1);
        sender.sendMessage("Executing...");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        sender.sendMessage("Done");
        return CommandResult.SUCCESS;
    }
}
