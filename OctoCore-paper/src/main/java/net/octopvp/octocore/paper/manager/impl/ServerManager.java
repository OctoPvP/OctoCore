package net.octopvp.octocore.paper.manager.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.PlayerProfile;
import net.octopvp.octocore.paper.objects.ServerData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ServerManager implements Manager {
    private Set<ServerData> connectedServers = new HashSet<>();

    public ServerData createServerData(String name) {
        if (getServerData(name) != null) return null;
        this.connectedServers.add(new ServerData(name));
        return getServerData(name);
    }

    public ServerData getServerData(String name) {
        return this.connectedServers.stream().filter(serverData -> serverData.getServerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<GlobalPlayer> getGlobalPlayers() {
        List<GlobalPlayer> players = new ArrayList<>();
        this.connectedServers.forEach(serverData -> players.addAll(serverData.getOnlinePlayers()));
        return players;
    }

    public int getGlobalMaxPlayers() {
        int i = 0;
        for (ServerData serverData : this.connectedServers) {
            i += serverData.getMaxPlayers();
        }
        return i;
    }
    public GlobalPlayer getGlobalPlayer(String name) {
        GlobalPlayer globalPlayerReturn = null;
        for (ServerData server : this.connectedServers) {
            for (GlobalPlayer globalPlayer : server.getOnlinePlayers()) {
                if (globalPlayer.getName().equalsIgnoreCase(name)) {
                    globalPlayerReturn = globalPlayer;
                }
            }
        }
        return globalPlayerReturn;
    }
    public boolean isPlayerOnline(String name){
        boolean r = false;
        for (GlobalPlayer globalPlayer : getGlobalPlayers()) {
            if(globalPlayer.getName().equalsIgnoreCase(name)){
                r = true;
            }
        }
        return r;
    }
    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable(OctoCore plugin) {

    }
}
