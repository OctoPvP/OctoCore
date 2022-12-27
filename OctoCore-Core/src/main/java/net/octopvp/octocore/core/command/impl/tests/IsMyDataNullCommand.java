package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

public class IsMyDataNullCommand {
    @Command(name = "ismydatanull")
    @PlayerOnly
    public CommandResult execute(@Sender Player sender) {
        boolean isNull = PlayerManager.getInstance().getData(sender) == null;
        sender.sendMessage("Your data is " + (isNull ? "null" : "not null"));
        if (!isNull) {
            PlayerData data = PlayerManager.getInstance().getData(sender);
            sender.sendMessage("Data loaded: " + data.isLoaded());
            sender.sendMessage("Your current rank is: " + data.getHighestRank().getColor() + data.getHighestRank().getName());
        }
        return CommandResult.SUCCESS;
    }
}
