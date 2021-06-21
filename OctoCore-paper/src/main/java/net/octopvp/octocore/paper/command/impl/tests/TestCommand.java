package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.command.Completer;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.permission.Permission;
import net.octopvp.octocore.paper.utils.permission.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class TestCommand extends BaseCommand {
    private static final String perm = PermissionUtil.fromEnum(Permission.COMMAND_NICK);
    @Command(name = "test", description = "test", aliases = {"test1","test2"},usage = "/test",permission = Permission.NOTHING)
    public CommandResult execute(Sender sender, String[] args) {
        PlayerData data = PlayerManager.getProfile(sender.getPlayer().getUniqueId());
        for (String allowedTag : data.getAllowedTags()) {
            PlayerTag tag = TagManager.getTag(allowedTag);
            sender.sendMessage(" - " + allowedTag + " | " + tag.getName());
        }
        return CommandResult.SUCCESS;
    }

    @Completer(name = "test")
    public List<String> tabComplete(Sender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Works");
        return list;
    }
}
