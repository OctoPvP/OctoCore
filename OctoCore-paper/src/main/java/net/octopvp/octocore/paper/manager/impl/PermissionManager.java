package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.permissions.Node;

import java.util.Collection;
import java.util.Set;

public class PermissionManager extends Manager {
    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
    public static boolean hasWildCardPermission(String perm, Collection<Node> nodes){
        boolean b = false;
        for (Node node : nodes) {
            if (node.isWildCard())
                if (node.getPermission().substring(0,node.getPermission().length() -1).startsWith(perm))
                    return false;
        }
        return b;
    }
    public static boolean isWildCard(String perm){
        return perm.endsWith("*");
    }
}
