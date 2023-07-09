package net.octopvp.octocore.core.command.impl.rank;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Name;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.annotation.Sync;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.menus.grant.MainGrantMenu;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

public class GrantCommand {
    @Command(name = "grant")
    @Sync
    @Permission(Permissions.GRANT)
    public CommandResult execute(@Sender Player sender, @Name("target") PlayerData target) {
        if (!target.isLoaded()) {
            target.loadGrants();
        }
        new MainGrantMenu(target).open(sender);
        return CommandResult.SUCCESS;
    }
}
