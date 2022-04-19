package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.player.PlayerMessagePacket;
import net.octopvp.octocore.paper.module.impl.punishments.util.Alt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor

public class GlobalPlayer {
    private final UUID uuid;
    private final String name;

    private String server, firstJoined, lastServer, address, rankName;
    private boolean vanished, staffChatAlerts, adminChatAlerts, reportAlerts, leaving, op;
    private long lastSeen, lastActivity = -1L;
    private List<PlayerTag> allTags = new ArrayList<>();
    private Map<String, ServerContext> permissions = new ConcurrentHashMap<>();
    private Map<String, ServerContext> negatedPermissions = new ConcurrentHashMap<>();
    private List<Alt> alts = new ArrayList<>();
    private List<String> addresses = new ArrayList<>();
    private int rankWeight;

    public boolean isOnline() {
        return OctoCore.getInstance().getServerManager().getConnectedServers().stream().filter(serverData ->
                serverData.getNames().stream().map(String::toLowerCase).collect(Collectors.toList())
                        .contains(name.toLowerCase())).findFirst().orElse(null) != null;
    }

    public void sendMessage(String message) {
        new PlayerMessagePacket(name, message).send();
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public boolean hasPermission(String permission) {
        if (isOp()) return true;
        if (permissionNegated(permission))
            return false;
        return hasSetPermission(permission);
    }

    public boolean hasPermission(String permission, String server) {
        if (server.equalsIgnoreCase(OctoCore.getServerName()) || server.equalsIgnoreCase("$$this server$$"))
            if (isOp())
                return true;
        if (negatedPermissions.containsKey(permission))
            return negatedPermissions.get(permission).getServer().equalsIgnoreCase(server) || negatedPermissions.get(permission).isGlobal();
        if (permissions.containsKey(permission)) {
            return permissions.get(permission).getServer().equalsIgnoreCase(server) || permissions.get(permission).isGlobal();
        }
        return false;
    }

    public boolean permissionNegated(String permission) {
        if (negatedPermissions.containsKey(permission)) {
            return negatedPermissions.get(permission).isThisServer();
        }
        return false;
    }

    public boolean hasSetPermission(String permission) {
        if (permissionNegated(permission))
            return false;
        if (permissions.containsKey(permission)) {
            return permissions.get(permission).isThisServer();
        }
        return false;
    }
}
