package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.conversations.ConfirmConversation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CallGcCommand {
    @Command(name = "callgc")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(CommandSender sender) {
        if (sender instanceof Player)
            OctoCore.getConversationFactory().withFirstPrompt(new ConfirmConversation("call System.gc()? This may cause players to be kicked!", (bool) -> {
                if (bool) {
                    sender.sendMessage(CC.GREEN + "Calling System.gc(); This may cause players to be kicked.");
                    System.gc();
                } else {
                    sender.sendMessage(CC.RED + "Cancelled.");
                }
            })).withLocalEcho(false).buildConversation(((Player) sender)).begin();
        else System.gc();
        return CommandResult.SUCCESS;
    }
}
