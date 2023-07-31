package net.octopvp.octocore.common.manager;

import lombok.Getter;
import net.octopvp.octocore.common.interfaces.manager.IServerManager;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.object.ServerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultServerManagerImpl implements IServerManager {
    @Getter
    private final Set<ServerData> connectedServers = ConcurrentHashMap.newKeySet();

    @Override
    public boolean isOnline(UUID player) {
        return connectedServers.stream().anyMatch(serverData -> serverData.getOnlinePlayers().contains(player));
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
        return this.connectedServers.stream().map(ServerData::getOnlinePlayers).flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public OnlinePlayer getOnlinePlayer(UUID uuid) {
        return getOnlinePlayers().stream().filter(onlinePlayer -> onlinePlayer.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public OnlinePlayer getOnlinePlayer(String name) {
        return getOnlinePlayers().stream().filter(onlinePlayer -> onlinePlayer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
