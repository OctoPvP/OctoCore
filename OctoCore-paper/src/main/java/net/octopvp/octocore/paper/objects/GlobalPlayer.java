package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.json.JsonChain;
import net.octopvp.octocore.paper.OctoCore;

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

    private String name, server, firstJoined, lastServer;
    private boolean vanished,staffChatAlerts, adminChatAlerts, reportAlerts;
    private long lastSeen, lastActivity = -1L;
    private List<PlayerTag> allTags = new ArrayList<>();
    private Map<String, ServerContext> permissions = new ConcurrentHashMap<>();
    private Map<String, ServerContext> negatedPermissions = new ConcurrentHashMap<>();

    public boolean isOnline(){
        return OctoCore.getServerManager().getConnectedServers().stream().filter(serverData ->
                serverData.getNames().stream().map(String::toLowerCase).collect(Collectors.toList())
                        .contains(name.toLowerCase())).findFirst().orElse(null) != null;
    }
    public void sendMessage(String message) {
        OctoCore.getInstance().getRedisData().write(JedisAction.PLAYER_MESSAGE,
                new JsonChain().addProperty("name", this.name).addProperty("message", message).get());
    }
    public UUID getUniqueId(){
        return uuid;
    }
    public void setUniqueId(UUID u){
        uuid = u;
    }
    public boolean hasPermission(String permission) {
        if (permissionNegated(permission))
            return false;
        return hasSetPermission(permission);
    }
    public boolean hasPermission(String permission, String server){
        if (negatedPermissions.containsKey(permission))
            return negatedPermissions.get(permission).getServer().equalsIgnoreCase(server) || negatedPermissions.get(permission).isGlobal();
        if (permissions.containsKey(permission)){
            return permissions.get(permission).getServer().equalsIgnoreCase(server) || permissions.get(permission).isGlobal();
        }
        return false;
    }
    public boolean permissionNegated(String permission){
        if (negatedPermissions.containsKey(permission)){
            return negatedPermissions.get(permission).isThisServer();
        }
        return false;
    }
    public boolean hasSetPermission(String permission){
        if (permissionNegated(permission))
            return false;
        if (permissions.containsKey(permission)) {
            return permissions.get(permission).isThisServer();
        }
        return false;
    }
}
