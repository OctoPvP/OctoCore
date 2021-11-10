package net.octopvp.octocore.paper.command.impl.grant;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.menus.grant.AddGrantMenu;
import net.octopvp.octocore.paper.menus.grant.MainGrantMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class GrantCommand extends BaseCommand {
    @Command(name = "grant")
    public CommandResult execute(Sender sender, String[] args) {
        return e1(sender, args);
    }
    @Command(
            name = "g1"
    )
    public CommandResult e1(Sender sender,String[] args){
        sender.sendMessage(CC.GREEN + "Getting PlayerData...");
        PlayerManager.getOfflineData(args[0]).thenAcceptAsync((data)->{
            new MainGrantMenu(data).open(sender.getPlayer());

        });
        return CommandResult.SUCCESS;
    }
    //get player data

}
