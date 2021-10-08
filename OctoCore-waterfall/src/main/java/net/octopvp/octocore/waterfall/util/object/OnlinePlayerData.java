package net.octopvp.octocore.waterfall.util.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class OnlinePlayerData {
    private final UUID uuid;
    private Set<Node> nodes = new HashSet<>();
    private Map<String,Boolean> cachedPermResults = new HashMap<>();
    public boolean hasPermission(String perm){
        boolean result = false;
        if (cachedPermResults.containsKey(perm.toLowerCase()))
            return cachedPermResults.get(perm);
        result = PermissionCalculator.hasPermissionResult(perm,nodes).allowed();
        cachedPermResults.put(perm.toLowerCase(),result);
        return result;
    }
}
