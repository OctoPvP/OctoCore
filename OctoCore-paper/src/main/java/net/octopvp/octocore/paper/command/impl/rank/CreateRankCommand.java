package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.rank.create.CreateRankMenu;
import net.octopvp.octocore.paper.utils.Sender;

public class CreateRankCommand extends BaseCommand {
    @Command(name = "createrank",permission = Permission.CREATE_RANK,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length != 1){
            sender.sendMessage(CC.RED + "Usage: /createrank <name>\nNote: You may not use spaces or unicode.");
            return CommandResult.SUCCESS;
        }else
            new CreateRankMenu(args[0]).open(sender);
        return CommandResult.SUCCESS;
    }
}
