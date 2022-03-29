package net.octopvp.octocore.paper.objects;

import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import java.util.UUID;

public class OctoPermissible extends PermissibleBase {
    private final UUID uuid;
    private final PermissibleBase oldPermissibleBase;

    public OctoPermissible(Player player, PermissibleBase old) {
        super(player);
        uuid = player.getUniqueId();
        this.oldPermissibleBase = old;
    }

    @Override
    public boolean hasPermission(String inName) {
        if (uuid == null) {
            Logger.error("Perm check: UUID is null!");
            return false;
        }
        PlayerData data = PlayerManager.getInstance().getData(this.uuid);
        if (data == null) {
            Logger.error("PlayerData is null!");
            Thread.dumpStack();
            return false;
        }
        return data.hasPermission(inName);
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
