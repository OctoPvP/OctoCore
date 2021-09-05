package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.permissions.Node;
import net.octopvp.octocore.paper.objects.permissions.PermissionReason;
import net.octopvp.octocore.paper.objects.permissions.PermissionResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class PermissionManager extends Manager {
    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
    public static PermissionResult hasPermissionResult(String perm,Collection<Node> permissions){
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
            if (perm.equalsIgnoreCase("*") && allPerms)
                return new PermissionResult("*", "Has wildcard permission", PermissionReason.ALLOWED_WILDCARD);
            if (permission.getPermission().equalsIgnoreCase(perm)) { //permission is explicitly set
                return new PermissionResult(perm, "explicitly set", PermissionReason.fromBoolean(permission.isAllowed()));
            } else if (permission.getPermission().equalsIgnoreCase("*")) {
                allPerms = permission.isAllowed();
                allPermsNegated = permission.isNegated();
            } else if (permission.getPermission().endsWith(".*")) {
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
                    b.set(new PermissionResult(perm, permission + ".*", PermissionReason.ALLOWED_SUB_WILDCARD));
                else b.set(new PermissionResult(perm, permission + ".*", PermissionReason.NEGATED_SUB_WILDCARD));
                lastPassed.set(permission.toLowerCase());
            }
        });
        return b.get();
    }
}
