package net.octopvp.octocore.common.util.perms;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.ServerContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class Node {
    private String key;
    private Node parent;
    private Map<String, Node> children;
    private Optional<Boolean> negated;
    private Optional<ServerContext> serverContext;

    private transient String cachedPermissionString;

    public Node(String key, Node parent, Map<String, Node> children, Optional<Boolean> negated, Optional<ServerContext> serverContext) {
        this.key = key;
        this.parent = parent;
        this.children = children;
        this.negated = negated;
        this.serverContext = serverContext;
    }

    public static Node create(String perm, boolean negated, String... serverContexts) {
        return PermissionManager.getInstance().nodeFromPermission(perm, negated, serverContexts);
    }

    public boolean isSignificant() { // this node is actually set
        return negated.isPresent() || serverContext.isPresent() || children.size() == 0;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public Node findChild(String key) {
        return children.get(key);
    }

    public Node findWildCard() {
        Map.Entry<String, Node> stringNodeEntry = children.entrySet().stream()
                .filter((entry) -> entry.getKey().equals("*")).findFirst().orElse(null);
        if (stringNodeEntry != null)
            return stringNodeEntry.getValue();
        return null;
    }

    public boolean appliesTo(String... server) {
        if (server == null || server.length == 0) server = new String[]{ServerContext.getServerName()};
        String[] finalServer = server;
        return serverContext.map(context -> context.isServer(finalServer)).orElse(true);
    }

    public boolean isNegated(String... serverContext) {
        String first = serverContext.length > 0 ? serverContext[0] : null;
        if (Objects.equals(first, "*") || (first != null && first.equalsIgnoreCase("global"))
                || appliesTo(serverContext)) {
            return negated.orElse(false);
        }
        return false; // not this server
    }

    public boolean isNegatedIgnoreScope() {
        return negated.orElse(false);
    }

    public Node copySettingsFrom(Node anotherNode) {
        this.negated = anotherNode.negated;
        this.serverContext = anotherNode.serverContext;
        return this;
    }

    public Node copyUnsetSettingsFrom(Node anotherNode) {
        if (!this.negated.isPresent()) this.negated = anotherNode.negated;
        if (!this.serverContext.isPresent()) this.serverContext = anotherNode.serverContext;
        return this;
    }

    public Node merge(Node anotherNode) {
        if (!anotherNode.getKey().equals(getKey())) {
            throw new IllegalArgumentException("Node does not have the same key.");
        }
        if (anotherNode.hasChildren()) {
            // merge all child nodes and their properties into this node
            anotherNode.getChildren().forEach((name, n) -> {
                Node child = this.findChild(name);
                if (child == null) {
                    children.put(name, n);
                } else {
                    child.merge(n);
                }
            });
        } else { // TODO figure this crap out
            copyUnsetSettingsFrom(anotherNode);
        }
        return this;
    }

    public void addChild(String key, Node read) {
        children.put(key, read);
        read.parent = this;
    }

    public String getPermissionString() {
        if (cachedPermissionString != null) return cachedPermissionString;
        // traverse up the tree and build the permission string
        StringBuilder builder = new StringBuilder();
        Node current = this;
        while (current != null) {
            builder.insert(0, current.getKey() + ".");
            current = current.getParent();
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1); // Remove the trailing dot
        }
        return cachedPermissionString = builder.toString();
    }

    @Override
    public String toString() {
        return "Node{" +
                "key='" + key + '\'' +
                ", negated=" + negated +
                ", serverContext=" + serverContext +
                ", childrenLen=" + children.size() +
                ", parentKey=" + (parent != null ? parent.getKey() : "null") +
                '}';
    }

    public boolean isAllowed() {
        return isNegatedIgnoreScope();
    }
}
