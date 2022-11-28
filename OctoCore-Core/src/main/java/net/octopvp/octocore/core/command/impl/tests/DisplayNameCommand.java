package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Sender;

public class DisplayNameCommand {
    @Command(name = "whatsmydisplayname")
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        sender.sendMessage(sender.getPlayer().getDisplayName());
        PlayerData data = PlayerManager.getInstance().getData(sender.getPlayer());
        sender.sendMessage(data.getCurrentColor() + sender.getPlayer().getDisplayName());
        return CommandResult.SUCCESS;
    }
}
