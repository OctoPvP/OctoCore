package net.octopvp.octocore.paper.hologram.packets.v1_8;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.octopvp.octocore.paper.hologram.HologramLine;
import net.octopvp.octocore.paper.hologram.packets.HologramPacket;
import net.octopvp.octocore.paper.hologram.packets.HologramPacketProvider;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Minecraft18HologramPacketProvider implements HologramPacketProvider {
    public HologramPacket getPacketsFor(Location location, HologramLine line) {
        List<PacketContainer> packets = Collections.singletonList(createArmorStandPacket(line.getSkullId(), line.getText(), location));
        return new HologramPacket(packets, Arrays.asList(Integer.valueOf(line.getSkullId()), Integer.valueOf(-1337)));
    }

    protected PacketContainer createArmorStandPacket(int witherSkullId, String text, Location location) {
        PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        StructureModifier<Integer> ints = displayPacket.getIntegers();
        ints.write(0, Integer.valueOf(witherSkullId));
        ints.write(1, Integer.valueOf(30));
        ints.write(2, Integer.valueOf((int) (location.getX() * 32.0D)));
        ints.write(3, Integer.valueOf((int) ((location.getY() - 2.0D) * 32.0D)));
        ints.write(4, Integer.valueOf((int) (location.getZ() * 32.0D)));
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, Byte.valueOf((byte) 32));
        watcher.setObject(2, ChatColor.translateAlternateColorCodes('&', text));
        watcher.setObject(3, Byte.valueOf((byte) 1));
        displayPacket.getDataWatcherModifier().write(0, watcher);
        return displayPacket;
    }
}
