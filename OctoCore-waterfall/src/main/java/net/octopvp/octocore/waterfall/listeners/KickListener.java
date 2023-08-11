package net.octopvp.octocore.waterfall.listeners;

import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.octopvp.octocore.waterfall.manager.LobbyManager;
import org.apache.commons.lang3.StringUtils;

public class KickListener implements Listener {
    @EventHandler(priority = 64)
    public void onKick(ServerKickEvent event) {
        String reason = event.getKickReason();
        if (StringUtils.containsIgnoreCase(reason, "kicked") ||
                StringUtils.containsIgnoreCase(reason, "restart") ||
                StringUtils.containsIgnoreCase(reason, "fallback") ||
                StringUtils.containsIgnoreCase(reason, "closed") ||
                StringUtils.containsIgnoreCase(reason, "disconnected") ||
                StringUtils.containsIgnoreCase(reason, "error")) {
            event.setCancelled(true);
            LobbyManager.sendToRandomLobby(event.getPlayer());
        }
    }
}
