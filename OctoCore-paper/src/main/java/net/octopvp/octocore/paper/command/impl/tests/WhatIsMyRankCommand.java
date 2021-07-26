package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class WhatIsMyRankCommand extends BaseCommand {
    @Command(name = "whatismyrank",playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        PlayerData data = PlayerManager.getData(sender.getPlayer().getUniqueId());
        sender.sendMessage(CC.AQUA + "Your highest grant/rank is " + data.getHighestRank().getDisplayName());
        return CommandResult.SUCCESS;
    }
}
