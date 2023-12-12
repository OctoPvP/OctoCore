package net.octopvp.octocore.common.interfaces;

import net.octopvp.octocore.common.util.perms.PermissionCheckResult;

public interface IPermissible {
    PermissionCheckResult calculatePermissionResult(String permission);
}
