package net.octopvp.octocore.core.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.RankManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadRanksCommand {
    @Command(name = "reloadranks")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(@Sender CommandSender sender) {
        RankManager.getInstance().reloadRanks();
        sender.sendMessage(CC.GREEN + "Done!");
        return CommandResult.SUCCESS;
    }
}
