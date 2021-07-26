package net.octopvp.octocore.paper.command.impl.essentials.gamemode;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.common.object.Permission;
import org.bukkit.GameMode;

public class SurvivalCommand extends BaseCommand {

    @Command(name = "survival",aliases = {"gms"},permission = Permission.SURVIVAL,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        sender.sendMessage(Lang.GAMEMODE.getMsg("SURVIVAL"));
        sender.getPlayer().setGameMode(GameMode.SURVIVAL);
        return CommandResult.SUCCESS;
    }
}
