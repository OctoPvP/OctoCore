package net.octopvp.octocore.paper.manager;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.objects.PlayerProfile;
import net.octopvp.octocore.paper.objects.ServerData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ServerManager implements Manager{
    private Set<ServerData> connectedServers = new HashSet<>();
    public ServerData createServerData(String name) {
        if (getServerData(name) != null) return null;
        this.connectedServers.add(new ServerData(name));
        return getServerData(name);
    }
    public ServerData getServerData(String name) {
        return this.connectedServers.stream().filter(serverData -> serverData.getServerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    public List<PlayerProfile> getGlobalPlayers() {
        List<PlayerProfile> players = new ArrayList<>();
        this.connectedServers.forEach(serverData -> players.addAll(serverData.getOnlinePlayers()));
        return players;
    }
    public PlayerProfile getProfile(String name){
        PlayerProfile returned = null;
        for (ServerData server : this.connectedServers) {
            for (PlayerProfile profile : server.getOnlinePlayers()) {
                if (profile.getName().equalsIgnoreCase(name)) {
                    returned = profile;
                }
            }
        }
        return returned;
    }
    public boolean isPlayerOnline(String name){
        boolean r = false;
        for (PlayerProfile globalPlayer : getGlobalPlayers()) {
            if(globalPlayer.getName().equalsIgnoreCase(name)){
                r = true;
            }
        }
        return r;
    }
    @Override
    public void init(OctoCorePaper plugin) {

    }

    @Override
    public void disable(OctoCorePaper plugin) {

    }
}
