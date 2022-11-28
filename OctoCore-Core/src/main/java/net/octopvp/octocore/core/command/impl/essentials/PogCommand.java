package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Cooldown;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.Sender;

public class PogCommand {
    @Command(name = "pog", aliases = {"poggers"})
    @Cooldown(69420)
    public CommandResult execute(Sender sender) {
        sender.sendMessage(CC.GREEN + "POGGERS");
        return CommandResult.SUCCESS;
    }
}
