package net.octopvp.octocore.common.util.permissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PermissionResult {
    private String permission,data;
    private PermissionReason reason;
    private long timestamp = System.currentTimeMillis();

    @Override
    public String toString() {
        return "{" +
                "permission:\"" + permission + '\"' +
                ", data:\"" + data + '\"' +
                ", reason:\"" + reason + '\"' +
                ", allowed:" + allowed() +
                ", timestamp:" + timestamp +
                '}';
    }
    public boolean allowed(){
        return reason == PermissionReason.ALLOWED || reason == PermissionReason.ALLOWED_WILDCARD || reason == PermissionReason.ALLOWED_SUB_WILDCARD;
    }
}
