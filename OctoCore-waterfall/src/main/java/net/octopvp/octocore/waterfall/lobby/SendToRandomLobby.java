package net.octopvp.octocore.waterfall.lobby;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.octopvp.octocore.common.RNG;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class SendToRandomLobby {
    public static void sendToRandomLobby(ProxiedPlayer player){
        HashMap<String, ServerInfo> hubServers = new HashMap<>();
        ProxyServer.getInstance().getServersCopy().forEach((k,v)->{
            if(StringUtils.containsIgnoreCase(k,"hub"))
                hubServers.put(k,v);
        });
        int server = RNG.getRandomInt(1,hubServers.size());
        ServerInfo serverInfo = (ServerInfo) hubServers.values().toArray()[server];
        if(player.getServer().getInfo() == serverInfo)
            player.sendMessage(new TextComponent(ChatColor.RED + "You are already connected to " + serverInfo.getName() + "! Please relog if this keeps on happening"));
        else player.connect(serverInfo);
    }
}
