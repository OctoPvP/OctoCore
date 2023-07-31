package net.octopvp.octocore.core.objects;

import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.permissions.PermissionReason;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class OctoPermissible extends PermissibleBase {
    private final UUID uuid;
    private final PermissibleBase oldPermissibleBase;

    public OctoPermissible(Player player, UUID uuid, PermissibleBase old) {
        super(player);
        this.uuid = uuid;
        this.oldPermissibleBase = old;
    }

    @Override
    public boolean hasPermission(String inName) {
        if (uuid == null) {
            Logger.error("Perm check: UUID is null! Falling back to old permissible base.");
            return oldPermissibleBase.hasPermission(inName);
        }
        PlayerData data = PlayerManager.getInstance().getData(this.uuid);
        if (data == null) {
            Logger.error("PlayerData is null! - " + PlayerManager.getInstance().getPlayerProfiles().size() + " | " + Arrays.stream(PlayerManager.getInstance()
                    .getPlayerProfiles().keySet().toArray(new UUID[0])).map(UUID::toString).collect(Collectors.joining(", ")));
            Logger.error(Bukkit.isPrimaryThread());
            Thread.dumpStack();
            return oldPermissibleBase.hasPermission(inName);
        }
        PermissionResult result = data.calculatePermissionResult(inName);
        if (result.getReason() == PermissionReason.NOT_SET) isOp(); // return oldPermissibleBase.hasPermission(inName);
        return result.allowed();
    }

    @Override
    public boolean isOp() {
        return super.isOp();
    }

    public PermissibleBase getOldPermissibleBase() {
        return oldPermissibleBase;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return hasPermission(perm.getName());
    }

    @Override
    public void recalculatePermissions() {
        if (this.uuid == null) {
            Logger.error("UUID is null!");
            return;
        }
        PlayerData data = PlayerManager.getInstance().getData(this.uuid);
        if (data == null) {
            Logger.error("PlayerData is null!");
            Thread.dumpStack();
            return;
        }
        data.getCachedPermissions().clear();
    }

}
