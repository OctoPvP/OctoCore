package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.Sender;

public class SeenCommand {
    @Command(name = "seen", description = "When the player was last seen on the network")
    @Cooldown(3)
    @Permission(Permissions.SEEN)
    public CommandResult execute(Sender sender) {
        // /seen Badbird5907

        return CommandResult.SUCCESS;
    }
}
