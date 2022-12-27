package net.octopvp.octocore.core.command.impl.essentials.gamemode;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpectatorCommand {
    @Command(name = "spectator", aliases = {"gmsp"})
    @Permission(Permissions.SPECTATOR)
    @PlayerOnly
    public CommandResult execute(@Sender Player sender) {
        sender.sendMessage(Lang.GAMEMODE.getMsg("SPECTATOR"));
        sender.getPlayer().setGameMode(GameMode.SPECTATOR);
        return CommandResult.SUCCESS;
    }
}
