package net.octopvp.octocore.paper.objects.permissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.ServerContext;

@Getter
@Setter
@AllArgsConstructor
/**
 * represents a permission node
 */
public class Node {
    private String permission;
    private ServerContext scope;
    private boolean allowed;
    public ServerContext getServer(){
        return scope;
    }

    public boolean isNegated() {
        return !allowed;
    }
}
