package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class WhatIsMyRankCommand {
    @Command(name = "whatismyrank")
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        PlayerData data = PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId());
        sender.sendMessage(CC.AQUA + "Your highest grant/rank is " + data.getHighestRank().getDisplayName());
        return CommandResult.SUCCESS;
    }
}
