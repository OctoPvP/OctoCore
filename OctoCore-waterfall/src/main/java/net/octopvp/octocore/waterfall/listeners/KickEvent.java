package net.octopvp.octocore.waterfall.listeners;

import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.apache.commons.lang3.StringUtils;

public class KickEvent implements Listener {
    @EventHandler
    public void onKick(ServerKickEvent event){
        String reason = event.getKickReason();
        if(StringUtils.containsIgnoreCase(reason,"kicked") ||
                StringUtils.containsIgnoreCase(reason,"restart") ||
                StringUtils.containsIgnoreCase(reason,"fallback") ||
                StringUtils.containsIgnoreCase(reason,"closed") ||
                StringUtils.containsIgnoreCase(reason,"disconnected")){
            event.setCancelled(true);
        }
    }
}
