package net.octopvp.octocore.core.utils;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1f, 1f);
    }

    public static void playPing(Player player) {
        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1f, 1f);
    }

    public static void playError(Player p) {
        playSound(p, XSound.ENTITY_ENDERMAN_TELEPORT.parseSound());
    }

}
