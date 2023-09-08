package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;

import java.util.Arrays;
import java.util.Optional;

@Getter
@Setter
public class ServerContext {
    private String serversString;

    public String getServersString() {
        if (serversString == null || serversString.isEmpty())
            return "*";
        return serversString;
    }

    public String[] getServers() {
        return getServersString().split(",");
    }

    public static ServerContext global() {
        return new ServerContext("*");
    }

    public ServerContext(String server) {
        this.serversString = server;
        if (this.serversString == null || this.serversString.isEmpty()) {
            this.serversString = "*";
        }
    }

    public boolean isThisServer() {
        if (isGlobal()) return true;
        return Arrays.stream(getServersString().split(",")).anyMatch(s1 -> {
            String s = s1.trim().toLowerCase();
            return s.equalsIgnoreCase(OctoCoreCommon.getInstance().getServerName());
        });
    }

    public boolean isGlobal() {
        // return Arrays.stream(server.split(",")).anyMatch(s -> s.equalsIgnoreCase("global") || s.equals("*"));
        return Arrays.stream(getServersString().split(",")).anyMatch(s1 -> {
            String s = s1.toLowerCase().trim();
            return s.equals("*") || s.equalsIgnoreCase("global");
        });
    }

    public boolean isServer(String... in) {
        if (isGlobal()) return true;
        if (in == null || in.length == 0) {
            return false;
        }
        return Arrays.stream(getServersString().split(",")).anyMatch(s1 -> {  // TODO make sure global and * are handled properly
            String s = s1.toLowerCase().trim();
            if (s.equalsIgnoreCase("global"))
                s = "*";
            String finalS = s;
            return Arrays.stream(in).anyMatch(s2 -> {
                if (s2.equalsIgnoreCase("global"))
                    s2 = "*";
                return s2.equalsIgnoreCase(finalS);
            });
        });
    }

    public static boolean checkIsServer(Optional<ServerContext> ctx, String... in) {
        System.out.println("  - Checking if " + ctx + " is server " + Arrays.toString(in));
        if (ctx.isPresent()) {
            System.out.println("  - ctx is present");
            if (ctx.get().isGlobal()) {
                System.out.println("  - ctx is global");
                return true;
            }
            if (in == null || in.length == 0) {
                System.out.println("  - in is null or empty");
                return false;
            }
            return ctx.get().isServer(in);
        }
        return true; // if the server context is not present, it applies to all servers
    }

    public static String getServerName() {
        return OctoCoreCommon.getInstance().getServerName();
    }

    @Override
    public String toString() {
        return "ServerContext{" +
                "server='" + getServersString() + '\'' +
                '}';
    }
}
