package net.octopvp.octocore.paper.module.impl.auth;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.common.object.Permission;

public class AuthCommand extends BaseCommand {
    @Command(name = "2fa",playerOnly = true,aliases = {"auth"})
    public CommandResult execute(Sender sender, String[] args) {
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("setup")){
                if(sender.hasPermission(Permission.SETUP_2FA)) {
                    AuthModule.enableAuth(sender.getPlayer());
                    return CommandResult.SUCCESS;
                }
            }
        }
        if(AuthModule.isAuthed(sender.getPlayer())){
            sender.sendMessage(Lang.ALREADY_AUTHED);
            return CommandResult.SUCCESS;
        }
        else if(AuthModule.has2faEnabled(sender.getPlayer().getUniqueId())){
            if(args.length == 1){
                AuthModule.handle2FARequest(sender.getPlayer(),args[0]);
            }
        }else{
            sender.sendMessage(Lang.AUTH_NOT_ENABLED);
        }
        return CommandResult.SUCCESS;
    }
}
