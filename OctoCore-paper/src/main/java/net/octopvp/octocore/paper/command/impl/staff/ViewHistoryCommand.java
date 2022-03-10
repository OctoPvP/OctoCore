package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

public class ViewHistoryCommand extends BaseCommand { //FIXME fix o7
    @Command(name = "viewhistory", aliases = {"vh", "h"}, permission = Permission.VIEW_HISTORY, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 1) {
            /* FIXME
            try {
                if(!MojangUtil.doesPlayerExist(args[0]).get()){
                    return CommandResult.PLAYER_NOT_FOUND;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return CommandResult.MOJANG_ERROR;
            }
             */
            //new HistoryMenu(args[0]).openMenu(sender.getPlayer());
        } else return CommandResult.INVALID_ARGS;
        return CommandResult.SUCCESS;
    }

    @Command(name = "viewhistory.ban", playerOnly = true)
    public CommandResult executeBanHistory(Sender sender, String[] args) {
        return CommandResult.SUCCESS;
    }
}
