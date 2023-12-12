package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.ConsoleOnly;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.debug.Debugger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand {
    @Command(name = "debugexp")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(CommandSender sender, String[] args) {
        String expression = StringUtils.arrayToString(args);
        new Debugger(sender).execute(expression);
        return CommandResult.SUCCESS;
    }

    @Command(name = "debug")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult executeDbg(@Sender Player sender) {
        if (Logger.getDebugPlayers().contains(sender.getUniqueId()))
            Logger.getDebugPlayers().remove(sender.getUniqueId());
        else
            Logger.getDebugPlayers().add(sender.getUniqueId());
        sender.sendMessage(CC.GREEN + "Debug mode " + (Logger.getDebugPlayers().contains(sender.getUniqueId()) ? "enabled" : "disabled"));
        return CommandResult.SUCCESS;
    }

    @ConsoleOnly
    @Command(name = "logdebug")
    public CommandResult executeLog(@Sender CommandSender sender) {
        // set octocore.debug to true or false
        boolean debug = Boolean.getBoolean("octocore.debug");
        System.setProperty("octocore.debug", String.valueOf(!debug));
        sender.sendMessage(CC.BLUE + "Debug mode " + (debug ? CC.GREEN + "enabled" : CC.RED + "disabled"));
        return CommandResult.SUCCESS;
    }
}
