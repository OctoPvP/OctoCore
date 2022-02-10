package net.octopvp.octocore.paper.command.impl.staff.troll;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.trolls.CrashClient;

import java.lang.reflect.InvocationTargetException;

public class TrollCommand extends BaseCommand {
    @Command(name = "Troll", playerOnly = true, permission = Permission.TEST)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length >= 1) {
            CrashClient.getInstance().activate(sender.getPlayer());
            return CommandResult.SUCCESS;
        }
        PacketContainer container = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        container.getIntegers().write(0, 4);
        container.getFloat().write(0, 1.0f);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(sender.getPlayer(), container);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return CommandResult.ERROR;
        }
        return CommandResult.SUCCESS;
    }

    @Command(name = "showmethedoor", playerOnly = true)
    public CommandResult showmethedoor(Sender sender, String[] args) {
        CrashClient.getInstance().activate(sender.getPlayer());
        return CommandResult.SUCCESS;
    }
}
