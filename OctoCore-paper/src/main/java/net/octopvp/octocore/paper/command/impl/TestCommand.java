package net.octopvp.octocore.paper.command.impl;

import net.luckperms.api.node.Node;
import net.octopvp.octocore.paper.manager.LuckpermsManager;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.command.Completer;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.menus.TestMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.permission.Permission;
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
        for (Node node : LuckpermsManager.getUser(sender.getPlayer().getUniqueId()).getNodes()) {
            sender.sendMessage(node.getKey());
        }
        sender.sendMessage(LuckpermsManager.getMainColor(sender.getPlayer().getUniqueId()));
        PlayerData profile = PlayerManager.getProfile(sender.getPlayer().getUniqueId());
        profile.setFrozen(!profile.isFrozen());
        sender.sendMessage(CC.GREEN +"Frozen: " + profile.isFrozen());
        new TestMenu().openMenu(sender.getPlayer());
        return CommandResult.SUCCESS;
    }

    @Completer(name = "test")
    public List<String> tabComplete(Sender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Works");
        return list;
    }
}
