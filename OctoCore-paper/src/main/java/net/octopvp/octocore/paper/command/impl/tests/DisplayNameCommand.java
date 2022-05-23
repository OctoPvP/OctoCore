package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

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
