package net.octopvp.octocore.common.util.permissions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PermissionCalculator {
    public static final String ROOT_WILDCARD = "*",SUB_WILDCARD = ".*";

    public static PermissionResult hasPermissionResult(String perm, Collection<Node> permissions){
        return hasPermissionResult(perm,permissions,"$$this server$$");
    }
    public static PermissionResult hasPermissionResult(String perm, Collection<Node> permissions,String server) {
        PermissionResult b = new PermissionResult(perm, "not set", PermissionReason.NOT_SET);
        boolean allPerms = false;
        boolean allPermsNegated = false;
        Map<String, Boolean> wildcardPermissions = new HashMap<>();
        for (Node permission : permissions) {
            if (server == "$$this server$$"){
                if (!permission.getScope().isThisServer())
                    continue;
            }else if (!permission.getScope().isServer(server))
                continue;
            if (perm.equalsIgnoreCase(ROOT_WILDCARD) && allPerms)
                return new PermissionResult(ROOT_WILDCARD, "Has wildcard permission", PermissionReason.ALLOWED_WILDCARD);
            if (permission.getPermission().equalsIgnoreCase(perm)) { //permission is explicitly set
                return new PermissionResult(perm, "explicitly set", PermissionReason.fromBoolean(permission.isAllowed()));
            } else if (permission.getPermission().equalsIgnoreCase(ROOT_WILDCARD)) {
                allPerms = permission.isAllowed();
                allPermsNegated = permission.isNegated();
            } else if (permission.getPermission().endsWith(SUB_WILDCARD)) {
                boolean allowed = permission.isAllowed();
                wildcardPermissions.put(permission.getPermission().substring(0, permission.getPermission().length() - 2), allowed);
            }
        }

        if (b.getReason() == PermissionReason.NOT_SET) b = wildCardCheck(wildcardPermissions, perm);
        //set to allowed wildcard only if the permission is not set and they have the root wildcard permission
        if (b.getReason() == PermissionReason.NOT_SET && allPerms) b = new PermissionResult(perm, "*", PermissionReason.ALLOWED_WILDCARD);
        else if (b.getReason() == PermissionReason.NOT_SET && allPermsNegated) b = new PermissionResult(perm, "*", PermissionReason.NEGATED_WILDCARD);
        return b;
    }

    private static PermissionResult wildCardCheck(Map<String, Boolean> map, String perm) {
        AtomicReference<PermissionResult> b = new AtomicReference<>(new PermissionResult(perm, "not set", PermissionReason.NOT_SET));
        AtomicReference<String> lastPassed = new AtomicReference<>(null);
        map.forEach((permission, allowed) -> {
            if (perm.toLowerCase().startsWith(permission.toLowerCase())) {
                if (lastPassed.get() != null) {
                    String last = lastPassed.get();
                    if (permission.length() < last.length())
                        return;
                }
                if (allowed)
                    b.set(new PermissionResult(perm, permission + SUB_WILDCARD, PermissionReason.ALLOWED_SUB_WILDCARD));
                else b.set(new PermissionResult(perm, permission + SUB_WILDCARD, PermissionReason.NEGATED_SUB_WILDCARD));
                lastPassed.set(permission.toLowerCase());
            }
        });
        return b.get();
    }
}
