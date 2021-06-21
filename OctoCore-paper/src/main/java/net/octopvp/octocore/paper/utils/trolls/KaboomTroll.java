package net.octopvp.octocore.paper.utils.trolls;

import net.octopvp.octocore.paper.utils.Utils;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class KaboomTroll implements Troll{
    @Override
    public void activate(Player player) {
        int a = Utils.getBlockAbove(player);
        if (a >= 65) { //no blocks obstructing
            kaboom(player);
            return;
        }else {
            kaboom(player,a - 1);
        }
    }
    public static void kaboom(Player player){
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.setVelocity(new Vector(0, 64, 0));
        player.setFallDistance(-65.0F);
        player.sendMessage(Lang.KABOOM.getMsg());
    }
    public static void kaboom(Player player,int i){
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.setVelocity(new Vector(0, i - 1, 0));
        player.setFallDistance(-i);
        player.sendMessage(Lang.KABOOM.getMsg());
    }
}
