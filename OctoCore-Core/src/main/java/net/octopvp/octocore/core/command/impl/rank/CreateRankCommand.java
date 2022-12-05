package net.octopvp.octocore.core.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.menus.impl.rank.EditRankMenu;
import net.octopvp.octocore.core.utils.Sender;

public class CreateRankCommand {
    @Command(name = "createrank")
    @Permission(Permissions.CREATE_RANK)
    @PlayerOnly
    public CommandResult execute(Sender sender, String name) {
        if (RankManager.getInstance().getRankByName(name) != null) {
            sender.sendMessage(CC.RED + "That rank already exists!");
            return CommandResult.SUCCESS;
        }
        new EditRankMenu(name).open(sender);
        return CommandResult.SUCCESS;
    }
}
