package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;

@Getter
@Setter
public class ServerContext {
    private String server;
    public ServerContext(String s){
        this.server = s;
    }
    public boolean isThisServer(){
        return server.equalsIgnoreCase(OctoCoreCommon.getServerName()) || server.equalsIgnoreCase("global");
    }
    public boolean isBungee(){
        return server.equalsIgnoreCase("bungee");
    }
    public boolean isGlobal(){
        return server.equalsIgnoreCase("global");
    }
}
