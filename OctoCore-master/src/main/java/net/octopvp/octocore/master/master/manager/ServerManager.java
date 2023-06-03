package net.octopvp.octocore.master.master.manager;

import lombok.Getter;
import net.octopvp.octocore.common.interfaces.manager.IServerManager;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.ServerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
            }
        }
        return r;
    }

    @Override
    public boolean isPlayerOnline(UUID uuid) {
        boolean r = false;
        for (GlobalPlayer globalPlayer : getGlobalPlayers()) {
            if (globalPlayer.getUuid().equals(uuid)) {
                r = true;
            }
        }
        return r;
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
