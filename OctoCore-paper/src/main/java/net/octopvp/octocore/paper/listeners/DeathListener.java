package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        if(e.getEntity().isDead()){
            if(OctoCorePaper.getInstance().getConfig().getBoolean("settings.auto-respawn"))
                e.getEntity().spigot().respawn();
        }
    }
}
