package net.octopvp.octocore.common.object.builders;

import lombok.Getter;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.util.perms.Node;

import java.util.Optional;

@Getter
public class NodeBuilder implements Cloneable {
    private String permission = "Not Set";
    private Optional<ServerContext> scope = Optional.empty();
    private boolean negated = false;
    private int weight = 0;

    public NodeBuilder(Node node) {
        permission = node.getPermissionString();
        scope = node.getServerContext();
        negated = node.getNegated().orElse(false);
    }

    public NodeBuilder() {
    }

    @Override
    public NodeBuilder clone() throws CloneNotSupportedException {
        return (NodeBuilder) super.clone();
    }

    public NodeBuilder setPermission(String perm) {
        this.permission = perm;
        return this;
    }

    public NodeBuilder setScope(String scope) {
        this.scope = Optional.of(new ServerContext(scope));
        return this;
    }

    public NodeBuilder setScope(ServerContext context) {
        this.scope = Optional.ofNullable(context);
        return this;
    }

    public NodeBuilder setNegated(boolean negated) {
        this.negated = negated;
        return this;
    }

    public Node build() {
        if (permission.equalsIgnoreCase("Not Set")) {
            throw new IllegalArgumentException("Permission is not set");
        }
        return Node.create(
                permission,
                negated,
                scope.map(ServerContext::getServersString).orElse("*")
        );
    }
}
