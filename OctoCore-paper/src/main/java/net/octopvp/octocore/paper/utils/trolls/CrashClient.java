package net.octopvp.octocore.paper.utils.trolls;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Actually crashes the client pog
 */
public class CrashClient {
    public void crashPlayer(Player p) {
        EntityPlayer v = ((CraftPlayer) p).getHandle();
        /*
        Packet53BlockChange deathPacket = new Packet53BlockChange();
        deathPacket.a = (int) p.getLocation().getX();
        deathPacket.b = (int) p.getLocation().getY();
        deathPacket.c = (int) p.getLocation().getZ();
        deathPacket.data = 0;
        deathPacket.material = 900; //invalid block id
        deathPacket.lowPriority = false;

        v.playerConnection.sendPacket(deathPacket);deathPacket
         */
    }
}
