package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

public class DisplayNameCommand {
    @PlayerOnly
    @Command(name = "whatsmydisplayname")
    public CommandResult execute(@Sender Player sender) {
        sender.sendMessage(sender.getDisplayName());
        PlayerData data = PlayerManager.getInstance().getData(sender);
        sender.sendMessage(data.getCurrentColor() + sender.getDisplayName());
        return CommandResult.SUCCESS;
    }
}
