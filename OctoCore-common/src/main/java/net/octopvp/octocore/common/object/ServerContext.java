package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;

@Getter
@Setter
public class ServerContext {
    private String server;

    public ServerContext(String s) {
        this.server = s;
    }

    public static ServerContext global() {
        return new ServerContext("Global");
    }

    public boolean isThisServer() {
        return server.equalsIgnoreCase(OctoCoreCommon.getInstance().getServerName()) || server.equalsIgnoreCase("global");
    }

    public boolean isBungee() {
        return server.equalsIgnoreCase("bungee") || isGlobal();
    }

    public boolean isGlobal() {
        return server.equalsIgnoreCase("global");
    }

    public boolean isServer(String in) {
        if (in.equalsIgnoreCase("global"))
            return true;
        String[] servers = in.split("\\|");
        for (String s : servers) {
            if (isServer(s))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ServerContext{" +
                "server='" + server + '\'' +
                '}';
    }
}
