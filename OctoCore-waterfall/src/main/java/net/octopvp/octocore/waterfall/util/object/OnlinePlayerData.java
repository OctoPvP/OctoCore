package net.octopvp.octocore.waterfall.util.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;
import net.octopvp.octocore.waterfall.util.GsonType;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class OnlinePlayerData {
    private final UUID uuid;
    private Set<Node> nodes = new HashSet<>();
    private Map<String, Boolean> cachedPermResults = new HashMap<>();
    private boolean frozen = false, vanished = false;

    public boolean hasPermission(String perm) {
        //Logger.debug("Checking permission for " + uuid + ": " + perm);
        boolean result = false;
        if (cachedPermResults.containsKey(perm.toLowerCase())) {
            //Logger.debug(" - Cached: " + cachedPermResults.get(perm.toLowerCase()));
            return cachedPermResults.get(perm);
        }
        result = PermissionCalculator.hasPermissionResult(perm, nodes).allowed();
        //Logger.debug(" - Result: " + result);
        cachedPermResults.put(perm.toLowerCase(), result);
        return result;
    }

    public boolean isPermSet(String perm) {
        return nodes.stream().anyMatch(node -> node.getPermission().equalsIgnoreCase(perm));
    }

    public void unSetPerm(String perm) {
        nodes.removeIf(node -> node.getPermission().equalsIgnoreCase(perm));
    }

    public void update() {
        Logger.debug("Updating for " + uuid);
        cachedPermResults.clear();
        nodes.clear();

        if (!OctoCoreWaterfall.getInstance().getRedisManager().isConnected()) return;

        try (Jedis jedis = OctoCoreWaterfall.getInstance().getRedisManager().getJedis()) {
            String json = jedis.hget("player-data", uuid.toString());
            //Logger.debug(json);
            if (json == null) return;
            Document document = Document.parse(json);
            if (document.containsKey("bungeePermissions")) {
                List<Node> permissions = OctoCoreWaterfall.getGson().fromJson(document.getString("bungeePermissions"), GsonType.NODE_LIST);
                /*.forEach(node -> {
                    Logger.debug(" - " + node.getPermission());
                });*/
                nodes.addAll(permissions);
            }
            this.vanished = document.getBoolean("vanished");
        }
    }

    @Override
    public String toString() {
        return "OnlinePlayerData{" +
                "uuid=" + uuid +
                ", nodes=" + OctoCoreWaterfall.getGson().toJson(nodes) +
                ", cachedPermResults=" + cachedPermResults +
                '}';
    }
}
