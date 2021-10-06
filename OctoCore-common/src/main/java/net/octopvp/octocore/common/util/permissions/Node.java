package net.octopvp.octocore.common.util.permissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.ServerContext;

import java.util.Locale;

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
    private int weight;
    public ServerContext getServer(){
        return scope;
    }

    public String getPermission() {
        return permission.toLowerCase();
    }

    public boolean isNegated() {
        return !allowed;
    }
    public boolean isWildCard(){
        return permission.equalsIgnoreCase("*") || permission.endsWith(".*");
    }
}
