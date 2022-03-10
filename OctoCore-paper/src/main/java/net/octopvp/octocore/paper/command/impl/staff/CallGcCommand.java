package net.octopvp.octocore.paper.command.impl.staff;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.conversations.ConfirmConversation;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.entity.Player;

public class CallGcCommand extends BaseCommand {
    @Command(name = "callgc", permission = Permission.CALL_GC)
    public CommandResult execute(Sender sender, String[] args) {
        if (sender.getCommandSender() instanceof Player)
            OctoCore.getConversationFactory().withFirstPrompt(new ConfirmConversation("call System.gc()? This may cause players to be kicked!", (bool) -> {
                if (bool) {
                    sender.sendMessage(CC.GREEN + "Calling System.gc(); This may cause players to be kicked.");
                    System.gc();
                } else {
                    sender.sendMessage(CC.RED + "Cancelled.");
                }
            })).withLocalEcho(false).buildConversation(sender.getPlayer()).begin();
        else System.gc();
        return CommandResult.SUCCESS;
    }
}
