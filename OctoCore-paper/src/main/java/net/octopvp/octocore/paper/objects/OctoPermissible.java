package net.octopvp.octocore.paper.objects;

import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;

import java.util.UUID;

public class OctoPermissible extends PermissibleBase {
    private final UUID uuid;
    private PermissibleBase oldPermissibleBase;
    public OctoPermissible(Player player,PermissibleBase old) {
        super(player);
        this.uuid = player.getUniqueId();
        this.oldPermissibleBase = old;
    }

    @Override
    public boolean hasPermission(String inName) {
        PlayerData data = PlayerManager.getData(this.uuid);
        if (data == null)
            return false;
        return data.hasPermission(inName);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return hasPermission(perm.getName());
    }
}
