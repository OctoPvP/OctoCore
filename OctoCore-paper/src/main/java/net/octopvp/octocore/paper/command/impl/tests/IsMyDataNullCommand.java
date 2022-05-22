package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class IsMyDataNullCommand {
    @Command(name = "ismydatanull", playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
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
