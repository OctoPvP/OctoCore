package net.octopvp.octocore.core.module.impl.auth;

import net.badbird5907.jdacommand.CommandResult;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.entity.Player;

public class AuthCommand {
    public CommandResult execute(@Sender Player sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("setup")) {
                if (sender.hasPermission(Permissions.SETUP_2FA)) {
                    AuthModule.enableAuth(sender.getPlayer());
                    return CommandResult.SUCCESS;
                }
            }
        }
        if (AuthModule.isAuthed(sender.getPlayer())) {
            sender.sendMessage(Lang.ALREADY_AUTHED.toString());
            return CommandResult.SUCCESS;
        } else if (AuthModule.has2faEnabled(sender.getPlayer().getUniqueId())) {
            if (args.length == 1) {
                AuthModule.handle2FARequest(sender.getPlayer(), args[0]);
            }
        } else {
            sender.sendMessage(Lang.AUTH_NOT_ENABLED.toString());
        }
        return CommandResult.SUCCESS;
    }
}
