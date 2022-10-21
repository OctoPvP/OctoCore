package net.octopvp.octocore.common.manager;

import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.ServerData;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IServerManager {
    boolean isOnline(GlobalPlayer player);

    ServerData createServerData(String name);

    ServerData getServerData(String name);

    List<GlobalPlayer> getGlobalPlayers();



    GlobalPlayer getGlobalPlayer(String name);

    GlobalPlayer getGlobalPlayer(UUID uuid);

    boolean isPlayerOnline(String name);

    boolean isPlayerOnline(UUID uuid);

    Collection<ServerData> getConnectedServers();
}
