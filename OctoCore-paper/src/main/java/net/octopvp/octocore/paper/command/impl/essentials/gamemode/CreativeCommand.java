package net.octopvp.octocore.paper.command.impl.essentials.gamemode;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.GameMode;

public class CreativeCommand {

    @Command(name = "creative", aliases = {"gmc"})
    @PlayerOnly
    @Permission(Permissions.CREATIVE)
    public CommandResult execute(Sender sender, String[] args) {
        sender.sendMessage(Lang.GAMEMODE.getMsg("CREATIVE"));
        sender.getPlayer().setGameMode(GameMode.CREATIVE);
        return CommandResult.SUCCESS;
    }
}
