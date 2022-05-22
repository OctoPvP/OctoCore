package net.octopvp.octocore.paper.command.impl.essentials.gamemode;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.GameMode;

public class CreativeCommand {

    @Command(name = "creative", aliases = {"gmc"}, permission = Permission.CREATIVE, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        sender.sendMessage(Lang.GAMEMODE.getMsg("CREATIVE"));
        sender.getPlayer().setGameMode(GameMode.CREATIVE);
        return CommandResult.SUCCESS;
    }
}
