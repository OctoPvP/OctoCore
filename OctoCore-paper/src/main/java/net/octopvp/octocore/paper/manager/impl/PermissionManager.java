package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.OctoPermissible;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.PermissionReason;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PermissionManager extends Manager {
    public static final String ROOT_WILDCARD = PermissionCalculator.ROOT_WILDCARD,SUB_WILDCARD = PermissionCalculator.SUB_WILDCARD;
    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
    public static void injectPermissible(Player player){
        UUID uuid = player.getUniqueId();
        Logger.debug("UUID: %1",uuid);
        PermissibleBase old = player.getPermissibleBase();
        PermissibleBase newBase = new OctoPermissible(player,old);
        player.setPermissibleBase(newBase);
        if (player.getPermissibleBase() instanceof OctoPermissible){
            Logger.info("Successfully injected permissible!");
        }else{
            Logger.error("Could not inject permissible!");
        }
    }
    public static boolean isWildcard(String perm) {
        return perm.equalsIgnoreCase(ROOT_WILDCARD) || perm.endsWith(SUB_WILDCARD);
    }

}
