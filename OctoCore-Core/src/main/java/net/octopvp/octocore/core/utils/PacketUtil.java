package net.octopvp.octocore.core.utils;

import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil {
    @Getter
    @Setter
    private static ProtocolManager protocolManager;

    public static void updateCurrentOpenInvTitle(final Player p, final String title) {
        Tasks.runAsync(() -> {
            EntityPlayer ep = ((CraftPlayer) p).getHandle();
            PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, "minecraft:chest", new ChatMessage(title), p.getOpenInventory().getTopInventory().getSize());
            ep.playerConnection.sendPacket(packet);
            /*
            PacketContainer pc = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
            pc.getIntegers().write(0,ep.activeContainer.windowId);
            pc.getStrings().write(0,"minecraft:chest");
            pc.getStrings().write(1,title);
            pc.getIntegers().write(1,p.getOpenInventory().getTopInventory().getSize());
            TODO: convert to protocollib
             */
            ep.updateInventory(ep.activeContainer);
        });
    }

}
