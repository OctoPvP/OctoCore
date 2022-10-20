package net.octopvp.octocore.paper.manager.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.manager.IServerManager;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class ServerManager extends Manager implements IServerManager {
    @Getter
    private Set<ServerData> connectedServers = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isOnline(GlobalPlayer player) {
        return getConnectedServers().stream().filter(serverData ->
                serverData.getNames().stream().map(String::toLowerCase).toList()
                        .contains(player.getName().toLowerCase())).findFirst().orElse(null) != null;
    }

    @Override
    public ServerData createServerData(String name) {
        if (getServerData(name) != null) return null;
        this.connectedServers.add(new ServerData(name));
        return getServerData(name);
    }

    @Override
    public ServerData getServerData(String name) {
        return this.connectedServers.stream().filter(serverData -> serverData.getServerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public List<GlobalPlayer> getGlobalPlayers() {
        List<GlobalPlayer> players = new ArrayList<>();
        this.connectedServers.forEach(serverData -> players.addAll(serverData.getOnlinePlayers()));
        return players;
    }

    @Override
    public GlobalPlayer getGlobalPlayer(String name) {
        for (ServerData server : this.connectedServers) {
            for (GlobalPlayer globalPlayer : server.getOnlinePlayers()) {
                if (globalPlayer.getName().equalsIgnoreCase(name)) {
                    return globalPlayer;
                }
            }
        }
        return null;
    }

    @Override
    public GlobalPlayer getGlobalPlayer(UUID uuid) {
        for (ServerData server : this.connectedServers) {
            for (GlobalPlayer globalPlayer : server.getOnlinePlayers()) {
                if (globalPlayer.getUuid().equals(uuid)) {
                    return globalPlayer;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isPlayerOnline(String name) {
        boolean r = false;
        for (GlobalPlayer globalPlayer : getGlobalPlayers()) {
            if (globalPlayer.getName().equalsIgnoreCase(name)) {
                r = true;
                break;
            }
        }
        return r;
    }

    public boolean isPlayerOnline(UUID uuid) {
        boolean r = false;
        for (GlobalPlayer globalPlayer : getGlobalPlayers()) {
            if (globalPlayer.getUuid().equals(uuid)) {
                r = true;
                break;
            }
        }
        return r;
    }

    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
}
