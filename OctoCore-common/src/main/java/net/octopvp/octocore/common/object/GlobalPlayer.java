package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.redis.packets.PlayerMessagePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter

public class GlobalPlayer {
    private UUID uuid;
    private String name;

    private String server, lastServer, address, rankName, coloredName;
    private boolean vanished, staffChatAlerts, adminChatAlerts, reportAlerts, leaving, op, staff;
    private long lastSeen, lastActivity = -1L, firstJoined = System.currentTimeMillis();
    private List<UUID> allTags = new ArrayList<>();
    private Map<String, ServerContext> permissions = new ConcurrentHashMap<>();
    private Map<String, ServerContext> negatedPermissions = new ConcurrentHashMap<>();
    private List<Alt> alts = new ArrayList<>();
    private List<String> addresses = new ArrayList<>();
    private List<Integer> ignored = new ArrayList<>();
    private int rankWeight;
    private UUID lastMessaged;

    private MessageSettings messageSettings = new MessageSettings();

    public GlobalPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public boolean isOnline() {
        return OctoCoreCommon.getInstance().getServerManager().getConnectedServers().stream().filter(serverData ->
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
        if (server.equalsIgnoreCase(OctoCoreCommon.getInstance().getServerImplementation().getServerName()) || server.equalsIgnoreCase("$$this server$$"))
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

    public boolean isIgnoring(String name) {
        if (name == null) return false;
        return messageSettings.getIgnoreList().stream().anyMatch(u -> u.equalsIgnoreCase(name));
    }
}
