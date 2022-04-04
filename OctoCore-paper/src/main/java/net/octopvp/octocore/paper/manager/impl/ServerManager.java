package net.octopvp.octocore.paper.manager.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.ServerData;

import java.util.*;
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
        this.serverData.entrySet().removeIf(next -> {
            if (System.currentTimeMillis() - next.getValue().getLastTick() >= 15000L) {
                if (!next.getValue().isSafelyStopped()) {
                    try {
                        new AdminAlertPacket().onReceive(new JsonBuilder().add("message", CC.RED + next.getValue().getServerName() + " may have crashed (has not responded for 15 seconds)").get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
            return false;
        });
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

    public GlobalPlayer getGlobalPlayer(UUID uuid) {
        return this.getGlobalPlayers().values().stream().filter(p -> p.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public GlobalPlayer getRealGlobalPlayer(String name) {
        return this.getRealGlobalPlayers().get(name.toLowerCase());
    }

    public boolean isPlayerOnline(String name) {
        return this.getGlobalPlayer(name) != null;
    }

    public boolean isPlayerOnline(UUID uuid) {
        return this.getGlobalPlayer(uuid) != null;
    }


    @Override
    public void init(OctoCore plugin) {
        instance = this;
    }

    @Override
    public void disable() {

    }
}
