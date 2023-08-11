package net.octopvp.octocore.waterfall.manager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class LobbyManager {
    public static void sendToRandomLobby(ProxiedPlayer player) {
        if (player.getServer() == null)
            return;
        ServerInfo serverInfo = getRandomlobby();
        if (serverInfo == null) {
            player.sendMessage(ChatColor.RED + "Could not find a lobby to warp you to! Please try again later!");
            return;
        }
        if (player.getServer().getInfo() == serverInfo)
            player.sendMessage(new TextComponent(ChatColor.RED + "You are already connected to " + serverInfo.getName() + "! Please relog if this keeps on happening"));
        else player.connect(serverInfo);
    }

    public static ServerInfo getRandomlobby() {
        AtomicReference<ServerInfo> returnServer = new AtomicReference<>();
        HashMap<String, ServerInfo> hubServers = new HashMap<>();
        ProxyServer.getInstance().getServersCopy().forEach((k, v) -> {
            if (StringUtils.containsIgnoreCase(k, "hub"))
                hubServers.put(k, v);
        });
        int server = Utilities.getRandomInt(0, hubServers.size()); //FIXME might need to minus 1 from the hub servers siz
        ServerInfo serverInfo;
        try {
            serverInfo = (ServerInfo) hubServers.values().toArray()[server];
        } catch (Exception e) {
            Logger.error("Could not find a hub server!");
            return null;
        }
        //try again if server is offline
        serverInfo.ping(((result, error) -> {
            if (error != null) {
                hubServers.remove(serverInfo.getName());
                int server1 = Utilities.getRandomInt(1, hubServers.size());
                ServerInfo serverInfo1 = (ServerInfo) hubServers.values().toArray()[server1];
                serverInfo1.ping((result1, error1) -> {
                    if (error1 != null)
                        returnServer.set(null);
                    else returnServer.set(serverInfo1);
                });
            } else returnServer.set(serverInfo);
        }));
        return returnServer.get();
    }
}
