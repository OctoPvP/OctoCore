package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.manager.impl.LuckpermsManager;
import net.octopvp.octocore.paper.utils.json.JsonChain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
@Setter
public class GlobalPlayer {
    private UUID uuid;

    private String name, server, rankName, firstJoined, lastServer;
    private boolean vanished,staffChatAlerts, adminChatAlerts, reportAlerts;
    private long lastSeen, lastActivity = -1L;
    private List<PlayerTag> allTags = new ArrayList<>();

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
    public CompletableFuture<Boolean> hasPermission(String node){
        if (!server.equals(OctoCore.getServerName())) {//not this server
            return LuckpermsManager.hasPermissionOffline(uuid,node);
        }
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        completableFuture.complete(LuckpermsManager.hasPermission(uuid,node));
        return completableFuture;
    }
}
