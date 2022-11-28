package net.octopvp.octocore.core.command.impl.essentials.gamemode;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.Sender;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.GameMode;

public class AdventureCommand {

    @Command(name = "adventure", aliases = {"gma"})
    @Permission(Permissions.ADVENTURE)
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        sender.sendMessage(Lang.GAMEMODE.getMsg("ADVENTURE"));
        sender.getPlayer().setGameMode(GameMode.ADVENTURE);
        return CommandResult.SUCCESS;
    }
}
