package net.octopvp.octocore.common.interfaces.manager;

import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.object.ServerData;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IServerManager {
    boolean isOnline(UUID player);
    boolean isOnline(String name);
    ServerData createServerData(String name);
    ServerData getServerData(String name);
    List<OnlinePlayer> getOnlinePlayers();
    Collection<ServerData> getConnectedServers();
    OnlinePlayer getOnlinePlayer(UUID uuid);
    OnlinePlayer getOnlinePlayer(String name);
}
