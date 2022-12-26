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

public class SurvivalCommand {
    @Command(name = "survival", aliases = {"gms"})
    @Permission(Permissions.SURVIVAL)
    @PlayerOnly
    public CommandResult execute(@Sender Player sender) {
        sender.sendMessage(Lang.GAMEMODE.getMsg("SURVIVAL"));
        sender.getPlayer().setGameMode(GameMode.SURVIVAL);
        return CommandResult.SUCCESS;
    }
}
