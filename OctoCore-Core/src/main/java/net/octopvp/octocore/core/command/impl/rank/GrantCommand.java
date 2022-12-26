package net.octopvp.octocore.core.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.menus.grant.MainGrantMenu;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

public class GrantCommand {
    @Command(name = "grant")
    @Permission(Permissions.GRANT)
    public CommandResult execute(@Sender Player sender, PlayerData target) {
        sender.sendMessage(CC.GREEN + "Getting PlayerData...");
        new MainGrantMenu(target).open(sender);
        return CommandResult.SUCCESS;
    }
}
