package net.octopvp.octocore.paper.manager.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.ServerData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class ServerManager extends Manager {
    @Getter
    private static ServerManager instance;
    private Map<String, ServerData> serverData = new ConcurrentHashMap<>();
    private Map<String, GlobalPlayer> globalPlayers = new ConcurrentHashMap<>();

    public Map<String, GlobalPlayer> getRealGlobalPlayers() {
        return this.globalPlayers;
    }

    public Map<String, GlobalPlayer> getGlobalPlayers() {
        return new HashMap<>(this.globalPlayers);
    }

    public Map<String, ServerData> getServerData() {
        return new HashMap<>(this.serverData);
    }

    public Set<ServerData> getConnectedServers() {
        return new HashSet<>(this.serverData.values());
    }

    public void removeInActivePlayers() {
        this.globalPlayers.entrySet().removeIf(next -> System.currentTimeMillis() - next.getValue().getLastActivity() >= 6000L);
    }

    public void removeInActiveServers() {
        this.serverData.entrySet().removeIf(next -> System.currentTimeMillis() - next.getValue().getLastTick() >= 15000L);
    }

    public ServerData createServerData(String name) {
        if (this.getServerData(name) != null) return this.getServerData(name);
        this.serverData.put(name.toLowerCase(), new ServerData(name));
        return this.getServerData(name);
    }

    public ServerData getServerData(String name) {
        return this.getServerData().get(name.toLowerCase());
    }

    public int getGlobalMaxPlayers() {
        return new HashMap<>(this.serverData).values().stream().mapToInt(ServerData::getMaxPlayers).sum();
    }

    public GlobalPlayer getGlobalPlayer(String name) {
        return this.getGlobalPlayers().get(name.toLowerCase());
    }

    public GlobalPlayer getRealGlobalPlayer(String name) {
        return this.getRealGlobalPlayers().get(name.toLowerCase());
    }

    @Override
    public void init(OctoCore plugin) {
        instance = this;
    }

    @Override
    public void disable() {

    }
}
