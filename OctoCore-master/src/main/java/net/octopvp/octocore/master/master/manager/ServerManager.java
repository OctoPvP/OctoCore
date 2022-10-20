package net.octopvp.octocore.master.master.manager;

import lombok.Getter;
import net.octopvp.aetheriacore.common.manager.IServerManager;
import net.octopvp.aetheriacore.common.object.GlobalPlayer;
import net.octopvp.aetheriacore.common.object.ServerData;
import net.octopvp.aetheriacoremaster.master.AetheriaCoreMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerManager implements IServerManager {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AetheriaCoreMaster.class);

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
}
