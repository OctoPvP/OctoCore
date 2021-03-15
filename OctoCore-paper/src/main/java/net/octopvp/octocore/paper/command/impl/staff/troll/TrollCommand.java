package net.octopvp.octocore.paper.command.impl.staff.troll;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class TrollCommand implements BaseCommand {
    @Command(name = "Troll", playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        container.getIntegers().write(0,4);
        container.getFloat().write(0,1.0f);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(sender.getPlayer(),container );
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}
