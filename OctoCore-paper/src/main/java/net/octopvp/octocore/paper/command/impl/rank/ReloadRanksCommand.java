package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.utils.Sender;

public class ReloadRanksCommand {
    @Command(name = "reloadranks")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(Sender sender) {
        RankManager.getInstance().reloadRanks();
        sender.sendMessage(CC.GREEN + "Done!");
        return CommandResult.SUCCESS;
    }
}
