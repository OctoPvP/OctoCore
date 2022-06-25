package net.octopvp.octocore.paper.command.impl.staff.troll;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Switch;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.trolls.CrashClient;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class TrollCommand {
    @Command(name = "troll")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(Sender sender, @Switch(value = "c", aliases = "crash") boolean crash) {
        if (crash) {
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

    @Command(name = "showmethedoor")
    @PlayerOnly
    public CommandResult showmethedoor(Player sender) {
        CrashClient.getInstance().activate(sender);
        return CommandResult.SUCCESS;
    }
}
