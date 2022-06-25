package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.menus.grant.MainGrantMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class GrantCommand {
    @Command(name = "grant")
    @Permission(Permissions.GRANT)
    public CommandResult execute(Sender sender, PlayerData target) {
        sender.sendMessage(CC.GREEN + "Getting PlayerData...");
        new MainGrantMenu(target).open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }

    /*
    @Command(name = "g1")
    @Permission(Permissions.GRANT)
    public CommandResult e1(Sender sender, String[] args) {
        sender.sendMessage(CC.GREEN + "Getting PlayerData...");
        PlayerData data = PlayerManager.getInstance().getOfflineData(args[0]);
        new MainGrantMenu(data).open(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
     */
}
