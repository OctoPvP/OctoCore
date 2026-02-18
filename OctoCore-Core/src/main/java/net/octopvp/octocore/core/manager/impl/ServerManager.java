package net.octopvp.octocore.core.manager.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.interfaces.manager.IServerManager;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class ServerManager extends Manager implements IServerManager {
    @Getter
    private Set<ServerData> connectedServers = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isOnline(UUID player) {
        return connectedServers.stream().anyMatch(serverData -> serverData.getOnlinePlayers().stream().anyMatch(onlinePlayer -> onlinePlayer.getUuid().equals(player)));
    }

    @Override
    public boolean isOnline(String name) {
        return connectedServers.stream().anyMatch(serverData -> serverData.getNames().contains(name));
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
    public List<OnlinePlayer> getOnlinePlayers() {
        Map<UUID, OnlinePlayer> players = new HashMap<>();
        for (ServerData connectedServer : connectedServers) {
            for (OnlinePlayer onlinePlayer : connectedServer.getOnlinePlayers()) {
                players.put(onlinePlayer.getUuid(), onlinePlayer);
            }
        }
        return new ArrayList<>(players.values());
    }

    @Override
    public OnlinePlayer getOnlinePlayer(UUID uuid) {
        return getOnlinePlayers().stream().filter(onlinePlayer -> onlinePlayer.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public OnlinePlayer getOnlinePlayer(String name) {
        return getOnlinePlayers().stream().filter(onlinePlayer -> onlinePlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
}
