package net.octopvp.octocore.paper.utils.permission;

import net.octopvp.octocore.common.object.Permission;

public class PermissionUtil {
    public static final String fromEnum(Permission permission) {
        return permission.getNode();
    }
}
