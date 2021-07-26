package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.command.Completer;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.menus.grant.AddGrantMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.utils.permission.PermissionUtil;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TestCommand extends BaseCommand {
    private static final String perm = PermissionUtil.fromEnum(Permission.COMMAND_NICK);
    @Command(name = "test", description = "test", aliases = {"test1","test2"},usage = "/test",permission = Permission.NOTHING)
    public CommandResult execute(Sender sender, String[] args) {
        Rank rankData = new Rank();
        rankData.setName("Owner");
        rankData.setPrefix("&7[&4OWNER&7]");
        rankData.setColor(ChatColor.DARK_RED);
        rankData.setWeight(100);
        rankData.setRankType(RankType.STAFF);
        rankData.save();
        RankManager.reloadRanks();
        return CommandResult.SUCCESS;
    }
    @Command(name = "test.t1")
    public CommandResult exec(Sender sender,String[] args) {
        sender.sendMessage(CC.GREEN + "Opening menu...");
        new AddGrantMenu(PlayerManager.getPlayerData(args[0])).open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }

    @Completer(name = "test")
    public List<String> tabComplete(Sender sender, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("Works");
        return list;
    }
}
