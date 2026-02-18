package net.octopvp.octocore.master.master.manager;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.octopvp.octocore.common.interfaces.manager.IServerManager;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.object.ServerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ServerManager implements IServerManager {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ServerManager.class);

    @Getter
    private static ServerManager instance;


    public ServerManager() {
        instance = this;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Starting server manager...");
        instance = this;
    }

    @Getter
    private final Set<ServerData> connectedServers = ConcurrentHashMap.newKeySet();


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

    public Set<ServerData> getDummyServerData() {
        Set<ServerData> serverData = ConcurrentHashMap.newKeySet();
        int randomServerCount = (int) (Math.random() * 10);
        for (int i = 0; i < randomServerCount; i++) {
            serverData.add(ServerData.createDummyData("Server" + (int) (Math.random() * 1000)));
        }
        return serverData;
    }
}
