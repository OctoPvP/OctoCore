package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.utils.Sender;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendPermToBungeeCommand extends BaseCommand {
    @Command(name = "sendpermtobungee",playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 1){
            RankManager.sendPermissionToBungee(
                    sender.getPlayer(),
                    sender.getName(),
                    args[0],
                    true,
                    "global"
            );
            sender.sendMessage("&aSent permission to BungeeCord!");
            return CommandResult.SUCCESS;
        }
        return CommandResult.INVALID_ARGS;
    }
    @Command(name = "testpluginmsg",playerOnly = true)
    public CommandResult execute0(Sender sender, String[] args) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(PluginMsgChannels.SubChannels.TEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sender.getPlayer().sendPluginMessage(OctoCore.getInstance(),(args.length == 1 ? PluginMsgChannels.BUNGEE : "test"),b.toByteArray());
        sender.sendMessage("&aSent plugin message to BungeeCord!");
        return CommandResult.SUCCESS;
    }
}
