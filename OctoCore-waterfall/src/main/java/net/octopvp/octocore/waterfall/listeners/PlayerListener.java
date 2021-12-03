package net.octopvp.octocore.waterfall.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.octopvp.octocore.waterfall.manager.OnlinePlayersManager;
import net.octopvp.octocore.waterfall.util.object.OnlinePlayerData;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PreLoginEvent event) {
        if (OnlinePlayersManager.getDataMap() == null)
            OnlinePlayersManager.setDataMap(new ConcurrentHashMap<>());
        OnlinePlayersManager.getDataMap().put(event.getConnection().getUniqueId(), new OnlinePlayerData(event.getConnection().getUniqueId()));
    }
    @EventHandler
    public void onLeave(PlayerDisconnectEvent event){
        OnlinePlayersManager.getDataMap().remove(event.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onSwitch(ServerSwitchEvent event){
        OnlinePlayerData  data = OnlinePlayersManager.getDataMap().get(event.getPlayer().getUniqueId());//new server will send us nodes again
        if (data == null) {
            OnlinePlayersManager.getDataMap().put(event.getPlayer().getUniqueId(), new OnlinePlayerData(event.getPlayer().getUniqueId()));
        }
        data.getNodes().clear();
        data.getCachedPermResults().clear();
    }
}
