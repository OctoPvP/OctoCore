package net.octopvp.octocore.waterfall.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.octopvp.octocore.common.PluginMsgChannels;

import java.io.*;

public class InComingChannelListener implements Listener {

    @EventHandler
    public void onPermissionRequest(PluginMessageEvent event) {
        try {
            String tag = event.getTag();
            if (tag.equalsIgnoreCase(PluginMsgChannels.SubChannels.PERMISSIONS)) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                String channel = in.readUTF();

                if (!channel.equals(PluginMsgChannels.PLUGIN_MSG)) {
                    return;
                }
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());

                String permission = in.readUTF();
                boolean set = Boolean.parseBoolean(in.readUTF());

                if (player != null) {
                    player.setPermission(permission, set);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
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

                System.out.println("Payload has been received");

                for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF(PluginMsgChannels.PLUGIN_MSG);
                        out.writeUTF(payload);
                    } catch (IOException e) {
                        System.out.println("Failed to send synchronization to spigot");
                    }
                    serverInfo.sendData(PluginMsgChannels.SubChannels.SYNC, b.toByteArray());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
