package net.octopvp.octocore.paper.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playSound(Player player, Sound sound){
        player.playSound(player.getLocation(),sound,1f,1f);
    }
    public static void playPing(Player player){
        player.playSound(player.getLocation(),Sound.ORB_PICKUP,1f,1f);
    }
    public static void playError(Player p){
        playSound(p,Sound.ENDERMAN_TELEPORT);
    }

}
