package net.octopvp.octocore.velocity.objects;

import com.velocitypowered.api.permission.Tristate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.common.util.GsonType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.perms.Node;
import net.octopvp.octocore.common.util.perms.PermissionCheckResult;
import net.octopvp.octocore.common.util.perms.PermissionManager;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class OnlinePlayerData {
    private final UUID uuid;
    private Map<String, Node> nodes = new HashMap<>();
    private Map<String, PermissionCheckResult> cachedPermResults = new HashMap<>();
    private boolean frozen = false, vanished = false, joinVanished = false;

    public Optional<Boolean> hasPermission(String perm) {
        String permission = perm.toLowerCase();
        Logger.debug("Checking permission for " + uuid + ": " + permission);
        PermissionCheckResult result;
        if (cachedPermResults.containsKey(permission)) {
            Logger.debug(" - Cached: " + cachedPermResults.get(permission));
            return cachedPermResults.get(permission).getAsTristate();
        }
        result = PermissionManager.getInstance().checkPermission(permission, nodes);
        Logger.debug(" - Result: " + result);
        cachedPermResults.put(permission, result);
        return result.getAsTristate();
    }

    public Node getNode(String perm) {
        return PermissionManager.getInstance().findNode(perm, nodes);
    }

    public boolean isPermSet(String perm) {
        return getNode(perm) != null;
    }

    public Tristate getTristate(String perm) {
        Optional<Boolean> optional = hasPermission(perm);
        return Tristate.fromNullableBoolean(optional.orElse(null));
    }

    public void update() {
        Logger.debug("Updating for " + uuid);
        cachedPermResults.clear();
        nodes.clear();

        if (!OctoCoreCommon.getInstance().getRedisManager().isConnected())
            return;

        Document data = DataCache.getData(uuid);
        if (data == null) {
            Logger.debug(" - No data found");
            return;
        }
        this.nodes = OctoCoreCommon.getInstance().getGson().fromJson(data.getString("calculated-nodes"),
                GsonType.NODE_MAP);
        if (data.containsKey("vanished"))
            this.vanished = data.getBoolean("vanished");
        if (data.containsKey("joinVanished"))
            this.joinVanished = data.getBoolean("joinVanished");
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
