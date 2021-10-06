package net.octopvp.octocore.common.util.permissions;

public enum PermissionReason {
    NEGATED,ALLOWED,NOT_SET,ALLOWED_WILDCARD,NEGATED_WILDCARD,ALLOWED_SUB_WILDCARD,NEGATED_SUB_WILDCARD;
    public static PermissionReason fromBoolean(boolean b){
        if (b)
            return ALLOWED;
        return NEGATED;
    }
}
