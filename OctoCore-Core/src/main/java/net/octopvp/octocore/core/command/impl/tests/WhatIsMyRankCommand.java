package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Sender;

public class WhatIsMyRankCommand {
    @Command(name = "whatismyrank")
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        PlayerData data = PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId());
        sender.sendMessage(CC.AQUA + "Your highest grant/rank is " + data.getHighestRank().getDisplayName());
        return CommandResult.SUCCESS;
    }
}
