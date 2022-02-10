package net.octopvp.octocore.paper.utils.trolls;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

public class DemoMenuTroll implements Troll {
    @Getter
    private static final DemoMenuTroll instance = new DemoMenuTroll();

    private DemoMenuTroll() {
    }

    @SneakyThrows
    @Override
    public void activate(Player player) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getIntegers().write(0, Integer.valueOf(5));
        packet.getFloat().write(0, Float.valueOf(0));
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }
}
