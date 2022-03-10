package net.octopvp.octocore.paper.objects;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Utils {
    public static int getBlockAbove(Player player) {
        int a = 256;
        for (int i = 0; i < player.getLocation().getBlockY(); i++) {
            Block block = player.getLocation().clone().add(0, i, 0).getBlock();
            if (!block.isEmpty()) {
                a = i;
                break;
            }
            continue;
        }
        return a;
    }
    /*
    int a = 0;
        for (int i = player.getLocation().getBlockY(); i < 256; i++) {
            Location location = player.getLocation();
            location.setY(i);
            if (location.getBlock().isEmpty())
                continue;
            a = i;
            break;
        }
        return a;
     */
}
