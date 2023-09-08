package net.octopvp.octocore.common.util.perms;

import java.util.HashMap;
import java.util.Map;

public class PermTest {

    public static void main(String[] args) {
        PermissionManager permissionManager = PermissionManager.getInstance();
        /*
        Object[] nodes = {
                //  perm   negated server
                "a.b.c", false, "*",
                "a.c.d", false, "*",
                "a.d.e", false, "*",
                "a.e.*", false, "lobby",
                "a.e.b", true, "*",
                "d.e.f", false, "*",
                "d.e.c.f", true, "*",

                "a.e.b.c", false, "lb1,lobby",
        };

        Map<String, Node> nodeMap = nodeTreeFromNodes(nodes);
        permissionManager.printNodeMap(nodeMap);

        Object[] nodes1 = {
                "a.b.c", true, "*",
                "a.c.d", true, "*",
                "a.d.e", true, "*",
                "a.e.b.c", false, "*"
        };
        Map<String, Node> nodeMap1 = nodeTreeFromNodes(nodes1);
        permissionManager.printNodeMap(nodeMap1);

        permissionManager.mergeNodeTrees(nodeMap, nodeMap1);

        checkPerms(permissionManager, nodeMap);
        checkPerms(permissionManager, nodeMap1);

         */
        Object[] nodes = {
                "*", false, "*",
                "a.b.c", true, "*",
        };
        Map<String, Node> nodeMap = nodeTreeFromNodes(nodes);
        permissionManager.printNodeMap(nodeMap);
        checkPerms(permissionManager, nodeMap);
    }

    public static void checkPerms(PermissionManager permissionManager, Map<String, Node> nodeMap) {
        System.out.println("------------------");
        // System.out.println(permissionManager.checkPermission("a.b.c", nodeMap, "lobby"));
        System.out.println(permissionManager.checkPermission("a.b.c", nodeMap, "lobby"));
        System.out.println(permissionManager.checkPermission("a.a.a", nodeMap, "lb1"));
        System.out.println(permissionManager.checkPermission("d.e.f", nodeMap, "lb1"));
        System.out.println(permissionManager.checkPermission("essentials.test.command", nodeMap, "lb1"));
    }

    public static Map<String, Node> nodeTreeFromNodes(Object[] nodes) {
        Map<String, Node> nodeMap = new HashMap<>();
        PermissionManager permissionManager = PermissionManager.getInstance();
        for (int i = 0; i < nodes.length; i += 3) {
            String perm = (String) nodes[i];
            boolean negated = (boolean) nodes[i + 1];
            String server = (String) nodes[i + 2];
            Node node = permissionManager.nodeFromPermission(perm, negated, server);
            String root = node.getKey();
            if (nodeMap.containsKey(root)) {
                nodeMap.get(root).merge(node);
            } else {
                nodeMap.put(root, node);
            }
        }
        return nodeMap;
    }
}
