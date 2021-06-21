package net.octopvp.octocore.paper.command.impl.essentials.gamemode;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.GameMode;

public class SpectatorCommand extends BaseCommand {

    @Command(name = "spectator",aliases = {"gmsp"},permission = Permission.SPECTATOR,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        sender.sendMessage(Lang.GAMEMODE.getMsg("SPECTATOR"));
        sender.getPlayer().setGameMode(GameMode.SPECTATOR);
        return CommandResult.SUCCESS;
    }
}
