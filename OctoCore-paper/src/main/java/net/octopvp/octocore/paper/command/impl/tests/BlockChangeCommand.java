package net.octopvp.octocore.paper.command.impl.tests;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BlockChangeCommand extends BaseCommand {
    @Command(name = "blockchange", playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        Player player = sender.getPlayer();
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutBlockChange change = new PacketPlayOutBlockChange(craftPlayer.getHandle().world, new BlockPosition(player.getX(), 257, player.getZ()));
        craftPlayer.getHandle().playerConnection.sendPacket(change);
        return CommandResult.SUCCESS;
    }
}
