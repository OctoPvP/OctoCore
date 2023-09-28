package net.octopvp.octocore.velocity.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.common.util.GsonType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.perms.Node;
import net.octopvp.octocore.common.util.perms.PermissionManager;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class OnlinePlayerData {
    private final UUID uuid;
    private Map<String, Node> nodes = new HashMap<>();
    private Map<String, Boolean> cachedPermResults = new HashMap<>();
    private boolean frozen = false, vanished = false;

    public boolean hasPermission(String perm) {
        //Logger.debug("Checking permission for " + uuid + ": " + perm);
        boolean result = false;
        if (cachedPermResults.containsKey(perm.toLowerCase())) {
            //Logger.debug(" - Cached: " + cachedPermResults.get(perm.toLowerCase()));
            return cachedPermResults.get(perm);
        }
        result = PermissionManager.getInstance().checkPermission(perm, nodes).allowed();
        //Logger.debug(" - Result: " + result);
        cachedPermResults.put(perm.toLowerCase(), result);
        return result;
    }

    public Node getNode(String perm) {
        return PermissionManager.getInstance().findNode(perm, nodes);
    }

    public boolean isPermSet(String perm) {
        return getNode(perm) != null;
    }


    public void update() {
        Logger.debug("Updating for " + uuid);
        cachedPermResults.clear();
        nodes.clear();

        if (!OctoCoreCommon.getInstance().getRedisManager().isConnected()) return;

        try (Jedis jedis = RedisManager.getJedis()) {
            String json = jedis.hget("player-data", uuid.toString());
            //Logger.debug(json);
            if (json == null) return;
            Document document = Document.parse(json);
            this.nodes = OctoCoreCommon.getInstance().getGson().fromJson(document.getString("nodes"), GsonType.NODE_MAP);
            this.vanished = document.getBoolean("joinVanished"); // used to be vanished but we've removed that from playerdata
        }
    }

    @Override
    public String toString() {
        return "OnlinePlayerData{" +
                "uuid=" + uuid +
                ", nodes=" + OctoCoreCommon.getInstance().getGson().toJson(nodes) +
                ", cachedPermResults=" + cachedPermResults +
                '}';
    }
}
