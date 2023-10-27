package net.octopvp.octocore.velocity.objects;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.velocity.manager.OnlinePlayersManager;

@RequiredArgsConstructor
public class OctoCorePermissionsProvider implements PermissionProvider, PermissionFunction {

    private final Player player;

    @Override
    public Tristate getPermissionValue(String permission) {
        OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(player.getUniqueId());
        if (data == null) return Tristate.UNDEFINED;
        return Tristate.fromOptionalBoolean(data.hasPermission(permission));
    }

    @Override
    public PermissionFunction createFunction(PermissionSubject subject) {
        Preconditions.checkState(subject == this.player, "createFunction called with different argument");
        return this;
    }
}
