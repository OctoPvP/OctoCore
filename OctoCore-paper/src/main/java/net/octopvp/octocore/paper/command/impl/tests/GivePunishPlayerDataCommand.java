package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class GivePunishPlayerDataCommand extends BaseCommand {
    @Command(name = "lmfao", playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        PunishPlayerData data = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(sender.getUniqueId());
        data.save();
        sender.sendMessage(String.valueOf(data));
        return CommandResult.SUCCESS;
    }
}
