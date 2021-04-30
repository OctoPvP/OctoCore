package net.octopvp.octocore.waterfall.util;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class Hubs {
    public ArrayList<ServerInfo> getHubs(){
        ArrayList<ServerInfo> hubServers = new ArrayList<>();
        ProxyServer.getInstance().getServersCopy().forEach((k, v)->{
            if(StringUtils.containsIgnoreCase(k,"hub"))
                hubServers.add(v);
        });
        return hubServers;
    }
    public HashMap<String,ServerInfo> getHubsAsHashMap(){
        HashMap<String, ServerInfo> hubServers = new HashMap<>();
        ProxyServer.getInstance().getServersCopy().forEach((k,v)->{
            if(StringUtils.containsIgnoreCase(k,"hub"))
                hubServers.put(k,v);
        });
        return hubServers;
    }
}
