package net.octopvp.octocore.paper.command.impl.essentials.gamemode;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand {


    @Command(name = "gamemode", aliases = {"gm"}, usage = "<gamemode> [player]")
    public CommandResult execute(Sender sender, GameMode gameMode, @Optional Player target) {
        if (!sender.hasPermission("octocore.command.gamemode." + gameMode.name().toLowerCase()))
            return CommandResult.NO_PERMS;
        if (target != null) {
            target.sendMessage(Lang.GAMEMODE.getMsg(gameMode.name()));
            target.setGameMode(gameMode);
            sender.sendMessage(CC.GREEN + "Set " + target.getName() + "'s gamemode to " + gameMode.name());
            return CommandResult.SUCCESS;
        }
        sender.sendMessage(Lang.GAMEMODE.getMsg(gameMode.name()));
        sender.getPlayer().setGameMode(gameMode);
        return CommandResult.SUCCESS;
    }
}
