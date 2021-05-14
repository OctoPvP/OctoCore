package net.octopvp.octocore.paper.utils;

import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil {
    public void updateCurrentOpenInvTitle(final Player p,final String title) {
        Tasks.runAsync(()->{
            EntityPlayer ep = ((CraftPlayer)p).getHandle();
            PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, "minecraft:chest", new ChatMessage(title), p.getOpenInventory().getTopInventory().getSize());
            ep.playerConnection.sendPacket(packet);
            ep.updateInventory(ep.activeContainer);
        });
    }
}
