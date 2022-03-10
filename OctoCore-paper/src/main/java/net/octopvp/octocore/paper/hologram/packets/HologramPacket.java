package net.octopvp.octocore.paper.hologram.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class HologramPacket {
    private final List<PacketContainer> packets;

    private final List<Integer> entityIds;

    public HologramPacket(List<PacketContainer> packets, List<Integer> entityIds) {
        this.packets = packets;
        this.entityIds = entityIds;
    }

    public void sendToPlayer(Player player) {
        for (PacketContainer packetContainer : this.packets) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Integer> getEntityIds() {
        return this.entityIds;
    }

    public List<PacketContainer> getPackets() {
        return this.packets;
    }
}
