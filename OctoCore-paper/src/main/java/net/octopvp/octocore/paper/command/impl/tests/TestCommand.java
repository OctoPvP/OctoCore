package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.command.Completer;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.menus.TestMenu;
import net.octopvp.octocore.paper.menus.grant.AddGrantMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.objects.builders.GrantBuilder;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.utils.permission.PermissionUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TestCommand extends BaseCommand {
    private static final String perm = PermissionUtil.fromEnum(Permission.COMMAND_NICK);
    @Command(name = "test", description = "test", aliases = {"test1","test2"},usage = "/test",playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        PlayerData data = PlayerManager.getData(sender.getPlayer().getUniqueId());
        if (data == null){
            sender.sendMessage(ChatColor.RED + "Data is null!");
            return CommandResult.SUCCESS;
        }
        Grant grant = new GrantBuilder(RankManager.getRankByName("Owner")).setActive(true).setPerm(true).setReason("lmao").setServer(ServerContext.global()).build();
        data.applyGrant(grant);
        sender.sendMessage(ChatColor.GREEN + "Done");
        return CommandResult.SUCCESS;
    }
}
