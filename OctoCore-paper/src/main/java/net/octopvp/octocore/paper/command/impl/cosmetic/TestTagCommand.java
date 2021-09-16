package net.octopvp.octocore.paper.command.impl.cosmetic;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;

public class TestTagCommand extends BaseCommand {
    @Command(name = "testtag",permission = Permission.ADMIN,playerOnly = true,usage = "<tag>")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length != 1 )
            return CommandResult.INVALID_ARGS;
        TagManager.setPlayerTag(sender.getPlayer(),args[0]);
        PlayerData pdata  = PlayerManager.getProfile(sender.getPlayer().getUniqueId());
        sender.sendMessage(CC.GREEN + "Set your tag to: " + pdata.getTagString() + "\n" + CC.GREEN + "Your chat message will now look like: \n" + pdata.getFormattedName(false) + CC.R + ": Hello World");
        return CommandResult.SUCCESS;
    }
}
