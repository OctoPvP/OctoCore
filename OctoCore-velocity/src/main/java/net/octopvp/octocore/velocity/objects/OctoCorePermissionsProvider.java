package net.octopvp.octocore.velocity.objects;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OctoCorePermissionsProvider implements PermissionProvider, PermissionFunction {

    private final Player player;
    private final OnlinePlayerData data;

    @Override
    public Tristate getPermissionValue(String permission) {
        return Tristate.fromOptionalBoolean(data.hasPermission(permission));
    }

    @Override
    public PermissionFunction createFunction(PermissionSubject subject) {
        Preconditions.checkState(subject == this.player, "createFunction called with different argument");
        return this;
    }
}
