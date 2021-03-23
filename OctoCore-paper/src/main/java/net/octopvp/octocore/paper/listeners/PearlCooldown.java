package net.octopvp.octocore.paper.listeners;

import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.spigot.api.events.EnderPearlThrowEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class PearlCooldown implements Listener {
    public static final HashMap<UUID, Long> cooldown = new HashMap<>();
    public static final int cooldownTime = OctoCorePaper.getInstance().getConfig().getInt("cooldown.pearl.time");
    @EventHandler(priority = EventPriority.NORMAL)
    public void onLaunch(EnderPearlThrowEvent e) {
        Player p = e.getPlayer();
        Logger.debug("Launched");
        if(cooldown.containsKey(p.getUniqueId())){
            double l = cooldown.get(p.getUniqueId()),i = OctoCorePaper.getInstance().getConfig().getInt("cooldown.pearl.time") * 1000, c = System.currentTimeMillis();
            if(l+i > c){
                e.setCancelled(true);
                p.sendMessage(CC.RED + "You are still on cooldown for " + CC.D_RED + (left(p)/1000) + CC.RED + " seconds.");
            }else{
                cooldown.remove(p.getUniqueId());
            }
        }
        else{
            cooldown.put(p.getUniqueId(),System.currentTimeMillis());
            new PearlCooldownRunnable(p);
            LunarClientAPICooldown.sendCooldown(p,"Enderpearl");
        }
    }
    private long left(Player p) {
        if (cooldown.containsKey(p.getUniqueId())) {
            double i = cooldownTime * 1000;
            double l = cooldown.get(p.getUniqueId());
            long c = System.currentTimeMillis();
            if (l + i > c) {
                return (long) (l + i - c);
            }
        }
        return 0;
    }
}
class PearlCooldownRunnable extends BukkitRunnable{

    private Player p;
    private int counter;

    public PearlCooldownRunnable(Player p) {
        this.p = p;
        counter = 2;
        runTaskTimerAsynchronously(OctoCorePaper.getInstance(), 0, 1);
    }

    @Override
    public void run() {
        if (p != null) {
            long left = left(p);
            p.setLevel((int) (left / 1000));
            p.setExp((float) (left / (PearlCooldown.cooldownTime * 1000)));
            counter--;
        } else
            cancel();
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        this.p = null;
    }

    private long left(Player p) {
        if (PearlCooldown.cooldown.containsKey(p.getUniqueId())) {
            double i = PearlCooldown.cooldownTime * 1000;
            double l = PearlCooldown.cooldown.get(p.getUniqueId());
            long c = System.currentTimeMillis();
            if (l + i > c) {
                return (long) (l + i - c);
            }
        }
        return 0;
    }
}
