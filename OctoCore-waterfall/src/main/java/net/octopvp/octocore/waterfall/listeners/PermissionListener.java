package net.octopvp.octocore.waterfall.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.object.PermUpdateType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.permissions.NodeBuilder;
import net.octopvp.octocore.waterfall.manager.OnlinePlayersManager;
import net.octopvp.octocore.waterfall.util.object.OnlinePlayerData;

import java.io.*;

public class PermissionListener implements Listener {

    @EventHandler
    public void onPermissionRequest(PluginMessageEvent event) {
        try {
            String tag = event.getTag();
            Logger.debug("Tag: %1\nLooking For: %2", tag, PluginMsgChannels.PLUGIN_MSG);
            if (tag.equalsIgnoreCase(PluginMsgChannels.PLUGIN_MSG)) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                String channel = in.readUTF();
                Logger.debug("is permissions tag\nChannel: %1", channel);
                if (!channel.equalsIgnoreCase(PluginMsgChannels.SubChannels.PERMISSIONS)) {
                    return;
                }
                PermUpdateType type = PermUpdateType.valueOf(in.readUTF());
                if (type == PermUpdateType.CLEAR_CACHE) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());
                    if (player != null) {
                        OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(player.getUniqueId());
                        data.getCachedPermResults().clear();
                    }
                    return;
                }
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());
                NodeBuilder nodeBuilder = new NodeBuilder();
                nodeBuilder.setPermission(in.readUTF()).setAllowed(Boolean.parseBoolean(in.readUTF())).setScope(in.readUTF());

                Logger.debug("Permission update: " + nodeBuilder.getPermission() + " | " + nodeBuilder.isAllowed());

                if (player != null) {
                    OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(player.getUniqueId());
                    if (data.isPermSet(nodeBuilder.getPermission()))
                        data.unSetPerm(nodeBuilder.getPermission());
                    data.getNodes().add(nodeBuilder.build());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPermCheck(PermissionCheckEvent event) {
        Logger.debug("Permission check: %1", event.getPermission());
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(player.getUniqueId());
            Logger.debug("Player: %1, Data: %2", player.getName(), data);
            if (data == null)
                return;
            boolean b = data.hasPermission(event.getPermission());
            Logger.debug(b);
            event.setHasPermission(b);
        }
    }

    @EventHandler
    public void onSyncRequest(PluginMessageEvent event) {
        try {
            String tag = event.getTag();
            if (tag.equalsIgnoreCase(PluginMsgChannels.PLUGIN_MSG)) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                String channel = in.readUTF();

                if (!channel.equals(PluginMsgChannels.SubChannels.SYNC)) {
                    return;
                }

                String payload = in.readUTF();

                Logger.debug("Payload has been received");

                for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF(PluginMsgChannels.PLUGIN_MSG);
                        out.writeUTF(payload);
                    } catch (IOException e) {
                        Logger.debug("Failed to send synchronization to spigot");
                    }
                    serverInfo.sendData(PluginMsgChannels.SubChannels.SYNC, b.toByteArray());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
