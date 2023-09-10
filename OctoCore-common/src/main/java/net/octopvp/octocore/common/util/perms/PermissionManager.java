package net.octopvp.octocore.common.util.perms;

import lombok.Getter;
import net.octopvp.octocore.common.object.ServerContext;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class PermissionManager {
    @Getter
    private static final PermissionManager instance = new PermissionManager();
    private PermissionManager() {
        if (instance != null) throw new IllegalStateException("Already initialized");
    }

    public Node nodeFromPermission(String perm, boolean negated, String... serverContexts) {
        if (perm == null || perm.isEmpty()) return null;
        perm = perm.toLowerCase();
        String serverContext = serverContexts.length > 0 ? StringUtils.join(serverContexts, ",") : null;
        String[] parts = StringUtils.split(perm, ".");
        Node last = null;
        Node root = null;
        for (int i = 0; i < parts.length; i++) {
            boolean isFinalNode = i == parts.length - 1;
            String key = parts[i];
            Node node = new Node(
                    parts[i], last, new HashMap<>(),
                    isFinalNode ? Optional.of(negated) : Optional.empty(),
                    isFinalNode && serverContext != null && !serverContext.isEmpty() && !serverContext.equals("*") && !serverContext.equalsIgnoreCase("global")
                            ? Optional.of(new ServerContext(serverContext)) : Optional.empty()
            );
            if (root == null) root = node;
            if (last != null) {
                last.getChildren().put(key, node);
            }
            last = node;
        }
        return root;
    }

    public void addNode(Node node, Map<String, Node> nodeMap) {
        if (node == null) return;
        if (nodeMap.containsKey(node.getKey())) {
            nodeMap.get(node.getKey()).merge(node);
        } else {
            nodeMap.put(node.getKey(), node);
        }
    }

    public List<Node> findSignificantNodes(Map<String, Node> nodeMap) { // find the "leaf" nodes
        List<Node> nodes = new ArrayList<>();
        nodeMap.forEach((key, node) -> {
            if (!node.hasChildren() || node.isSignificant()) nodes.add(node);
            if (node.hasChildren()) nodes.addAll(findSignificantNodes(node.getChildren()));
        });
        return nodes;
    }

    public void removePermission(String permission, Map<String, Node> nodeMap) {
        String[] parts = StringUtils.split(permission.toLowerCase(), ".");
        Node parent = null;
        Node last = null;
        // find the last node and remove it
        int i = 0;
        for (String key : parts) {
            if (last == null) {
                last = nodeMap.get(key);
                if (last == null) {
                    break;
                }
            } else {
                parent = last;
                last = last.findChild(key);
                if (last == null) {
                    break;
                }
            }
            i++;
        }
        if (i < parts.length) { // we didn't find the full path
            return;
        }
        if (last != null) {
            if (parent != null) {
                parent.getChildren().remove(last.getKey());
            } else {
                nodeMap.remove(last.getKey());
            }
        }
    }

    public void removeNode(Node node, Map<String, Node> nodeMap) {
        String perm = node.getPermissionString();
        removePermission(perm, nodeMap);
    }

    public void printNode(Node node, int recursion) {
        System.out.print("|");
        for (int i = 0; i < recursion * 4; i++) {
            System.out.print(" ");
        }
        System.out.print("  | ---> ");
        Optional<Boolean> negated = node.getNegated();
        System.out.print(node.getKey() + " | " + (negated.map(bool -> (bool ? "negated" : "not negated") + " | ").orElse("")));
        System.out.println();
        node.getChildren().forEach((k, v) -> {
            printNode(v, recursion + 1);
        });
    }

    public void printNodeTree(Node root, int recursion) {
        System.out.println("| ---> " + root.getKey());
        root.getChildren().forEach((k, v) -> printNode(v, recursion));
    }

    public void printNodeList(List<Node> nodes) {
        int recursion = 1;
        for (Node node : nodes) {
            printNodeTree(node, recursion);
        }
    }

    public void printNodeMap(Map<String, Node> map) {
        List<Node> nodes = new ArrayList<>(map.values());
        printNodeList(nodes);
    }

    public PermissionCheckResult checkPermission(String permission, Map<String, Node> nodes, String... currentServers) {
        if (permission.isEmpty()) return null;
        if (currentServers.length == 0) {
            currentServers = new String[]{ServerContext.getServerName()};
        }
        Map.Entry<Node, Node> entry = findNodeInternal(permission, nodes);
        Node last = entry.getKey();
        Node lastWildCard = entry.getValue();
        if (last != null && !ServerContext.checkIsServer(last.getServerContext(), currentServers)) {
            last = null;
        }
        if (lastWildCard != null && !ServerContext.checkIsServer(lastWildCard.getServerContext(), currentServers)) {
            lastWildCard = null;
        }
        if (last != null) { // explicitly set
            return new PermissionCheckResult(permission, false, last.getNegated(), last.getServerContext(), PermissionCheckResult.Reason.EXPLICIT_SET, last);
        }
        if (lastWildCard != null) { // wildcard somewhere in the path we took
            return new PermissionCheckResult(permission, true, lastWildCard.getNegated(), lastWildCard.getServerContext(), PermissionCheckResult.Reason.WILDCARD, lastWildCard);
        }
        return new PermissionCheckResult(permission, false, Optional.empty(), Optional.empty(), PermissionCheckResult.Reason.NOT_SET, null);
    }

    private Map.Entry<Node, Node> findNodeInternal(String permission, Map<String, Node> nodes) {
        String[] parts = StringUtils.split(permission.toLowerCase(), ".");
        Node last = null;
        Node lastWildCard = nodes.get("*");
        int i = 0;
        for (String key : parts) {
            i++;
            Node node;
            if (last == null) {
                node = nodes.get(key);
            } else {
                node = last.findChild(key);
                if (node != null) {
                    Node wildCard = node.findWildCard();
                    if (wildCard != null) lastWildCard = wildCard;
                } else {
                    i--; // we didn't find the node, so we don't want to increment i
                }
            }
            last = node;
        }

        if (i < parts.length) { // we didn't find the full path
            // System.out.println("i < parts.length (" + i + " < " + parts.length + ")");
            last = null;
        }
        return new AbstractMap.SimpleEntry<>(last, lastWildCard);
    }

    public Node findNode(String permission, Map<String, Node> nodes, boolean... allowWildcards) {
        boolean allowWildCard = allowWildcards.length > 0 && allowWildcards[0];
        Map.Entry<Node, Node> entry = findNodeInternal(permission, nodes);
        Node last = entry.getKey();
        Node lastWildCard = entry.getValue();
        if (last != null) { // explicitly set
            return last;
        }
        if (lastWildCard != null && allowWildCard) { // wildcard somewhere in the path we took
            return lastWildCard;
        }
        return null;
    }


    public void mergeNodeTrees(Map<String, Node> nodeMap, Map<String, Node> nodeMap1) { // nodeMap should take priority
        nodeMap1.forEach((key, node) -> {
            if (nodeMap.containsKey(key)) {
                nodeMap.get(key).merge(node);
            } else {
                nodeMap.put(key, node);
            }
        });
    }
}
