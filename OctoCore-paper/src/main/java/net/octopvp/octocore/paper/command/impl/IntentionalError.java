package net.octopvp.octocore.paper.command.impl;

import net.octopvp.octocore.paper.utils.errorhandling.ErrorData;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.HandleError;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public class IntentionalError implements BaseCommand {
    @Command(name = "error",description = "throws a intentional error to test hastebin stuff")
    public CommandResult execute(Sender sender, String[] args) {
        try{
            throwlol();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            HandleError.handlePlayerError(new ErrorData(),sender.getPlayer(),e,false);
            return CommandResult.SUCCESS;
        }
        return CommandResult.ERROR;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
    public static void throwlol() throws IllegalArgumentException{
        throw new IllegalArgumentException("intentional dw :)");
    }
}
