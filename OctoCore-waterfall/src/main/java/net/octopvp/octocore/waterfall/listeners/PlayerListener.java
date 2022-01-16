package net.octopvp.octocore.waterfall.listeners;

import io.sentry.Sentry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.waterfall.manager.OnlinePlayersManager;
import net.octopvp.octocore.waterfall.util.object.OnlinePlayerData;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(LoginEvent event) {
        if (OnlinePlayersManager.getDataMap() == null)
            OnlinePlayersManager.setDataMap(new ConcurrentHashMap<>());
        if (event.getConnection() == null || event.getConnection().getUniqueId() == null) {
            Logger.debug("Connection: " + event.getConnection());
            if (event.getConnection() != null)
                Logger.debug("Connection UUID: " + event.getConnection().getUniqueId());
            Logger.debug("what the shit");
            event.setCancelled(true);
            return;
        }
        if (OnlinePlayersManager.getDataMap() == null) {
            Logger.debug("bruh");
            event.setCancelled(true);
            return;
        }
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
    @EventHandler
    public void onFreezeThingy(PluginMessageEvent event){
        try {
            String tag = event.getTag();
            if (tag.equalsIgnoreCase(PluginMsgChannels.PLUGIN_MSG)) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                String channel = in.readUTF();
                if (channel.equalsIgnoreCase(PluginMsgChannels.SubChannels.FREEZE)){
                    String name = in.readUTF();
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
                    if (player == null)
                        return;
                    OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(player.getUniqueId());
                    if (data == null)
                        return;
                    data.setFrozen(in.readBoolean());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Sentry.isEnabled())
                Sentry.captureException(e);
        }
    }
}
