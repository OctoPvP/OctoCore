package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.RankManager;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendPermToBungeeCommand {
    @Command(name = "sendpermtobungee")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(@Sender Player sender) {
        RankManager.getInstance().resetBungeePerms(sender);
        sender.sendMessage("Done!");
        return CommandResult.SUCCESS;
    }

    @Command(name = "testpluginmsg")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute0(@Sender Player sender, String[] args) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(PluginMsgChannels.SubChannels.TEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sender.sendPluginMessage(OctoCore.getInstance(), (args.length == 1 ? PluginMsgChannels.BUNGEE : "test"), b.toByteArray());
        sender.sendMessage("&aSent plugin message to BungeeCord!");
        return CommandResult.SUCCESS;
    }
}
