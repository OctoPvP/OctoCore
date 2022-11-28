package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Sender;

public class IsMyDataNullCommand {
    @Command(name = "ismydatanull")
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        boolean isNull = PlayerManager.getInstance().getData(sender.getPlayer()) == null;
        sender.sendMessage("Your data is " + (isNull ? "null" : "not null"));
        if (!isNull) {
            PlayerData data = PlayerManager.getInstance().getData(sender.getPlayer());
            sender.sendMessage("Data loaded: " + data.isLoaded());
            sender.sendMessage("Your current rank is: " + data.getHighestRank().getColor() + data.getHighestRank().getName());
        }
        return CommandResult.SUCCESS;
    }
}
