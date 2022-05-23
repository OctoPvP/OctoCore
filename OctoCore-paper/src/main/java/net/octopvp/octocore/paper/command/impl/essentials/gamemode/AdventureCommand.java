package net.octopvp.octocore.paper.command.impl.essentials.gamemode;

import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.GameMode;

public class AdventureCommand {

    @Command(name = "adventure", aliases = {"gma"}, permission = Permissions.ADVENTURE, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        sender.sendMessage(Lang.GAMEMODE.getMsg("ADVENTURE"));
        sender.getPlayer().setGameMode(GameMode.ADVENTURE);
        return CommandResult.SUCCESS;
    }
}
