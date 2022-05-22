package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.utils.Sender;

public class ReloadRanksCommand {
    @Command(name = "reloadranks", permission = Permission.ADMIN)
    public CommandResult execute(Sender sender, String[] args) {
        RankManager.getInstance().reloadRanks();
        sender.sendMessage(CC.GREEN + "Done!");
        return CommandResult.SUCCESS;
    }
}
