package net.octopvp.octocore.common.util.permissions;

import lombok.Getter;
import net.octopvp.octocore.common.object.ServerContext;

@Getter
public class NodeBuilder implements Cloneable{
    private String permission = "Not Set";
    private ServerContext scope = new ServerContext("Global");
    private boolean allowed = true;
    private int weight = 0;

    @Override
    public NodeBuilder clone() throws CloneNotSupportedException {
        return (NodeBuilder) super.clone();
    }

    public NodeBuilder(Node node){
        permission = node.getPermission();
        scope = node.getScope();
        allowed = node.isAllowed();
    }
    public NodeBuilder(){}
    public NodeBuilder setPermission(String perm){
        this.permission = perm;
        return this;
    }
    public NodeBuilder setScope(String scope){
        this.scope = new ServerContext(scope);
        return this;
    }
    public NodeBuilder setScope(ServerContext context){
        this.scope = context;
        return this;
    }
    public NodeBuilder setAllowed(boolean a){
        this.allowed = a;
        return this;
    }
    public NodeBuilder setWeight(int weight){
        this.weight = weight;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public Node build(){
        if (permission.equalsIgnoreCase("Not Set")){
            throw new IllegalArgumentException("Permission is not set");
        }
        return new Node(permission,scope,allowed,weight);
    }
}
