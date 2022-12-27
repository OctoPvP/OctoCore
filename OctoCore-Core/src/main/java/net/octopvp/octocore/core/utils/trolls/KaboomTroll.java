package net.octopvp.octocore.core.utils.trolls;

import lombok.Getter;
import net.octopvp.octocore.core.objects.Utils;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class KaboomTroll implements Troll {
    @Getter
    private static final KaboomTroll instance = new KaboomTroll();

    private KaboomTroll() {
    }

    public static void kaboom(Player player) {
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.setVelocity(new Vector(0, 64, 0));
        player.setFallDistance(-65.0F);
        player.sendMessage(Lang.KABOOM.getMsg());
    }

    public static void kaboom(Player player, int i) {
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.setVelocity(new Vector(0, i - 1, 0));
        player.setFallDistance(-i);
        player.sendMessage(Lang.KABOOM.getMsg());
    }

    @Override
    public void activate(Player player) {
        int a = Utils.getBlockAbove(player);
        if (a >= 65) { //no blocks obstructing
            kaboom(player);
        } else {
            kaboom(player, a - 1);
        }
    }
}
