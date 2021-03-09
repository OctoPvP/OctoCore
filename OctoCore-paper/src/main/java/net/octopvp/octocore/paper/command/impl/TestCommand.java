package net.octopvp.octocore.paper.command.impl;

import net.octopvp.octocore.common.rank.LuckpermsManager;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.command.Completer;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.permission.Permission;
import net.octopvp.octocore.paper.utils.permission.PermissionAnnotation;
import net.octopvp.octocore.paper.utils.permission.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements BaseCommand {
    private static final String perm = PermissionUtil.fromEnum(Permission.COMMAND_NICK);
    @Command(name = "test", description = "test", aliases = {"test1","test2"},usage = "/test",permission = Permission.NOTHING)
    public CommandResult execute(Sender sender, String[] args) {
        if(args.length > 0){
            return CommandResult.INVALID_ARGS;
        }
        sender.sendMessage(LuckpermsManager.getMainColor(sender.getPlayer().getUniqueId()) + "Hello " + sender.getName());
        sender.sendMessage(LuckpermsManager.getMainColor(sender.getPlayer().getUniqueId()) + "Hello displayname: " + sender.getDisplayName());
        if(sender.hasPermission("a.b.c"))
            sender.sendMessage(CC.AQUA + "works!");
        else sender.sendMessage(CC.RED + "nope");
        sender.sendMessage(LuckpermsManager.getMainColor(sender.getPlayer().getUniqueId()));
        return CommandResult.SUCCESS;
    }

    @Completer(name = "test")
    public List<String> tabComplete(Sender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Works");
        return list;
    }
}
