package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.OctoPermissible;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.util.UUID;

public class PermissionManager extends Manager {
    public static final String ROOT_WILDCARD = PermissionCalculator.ROOT_WILDCARD, SUB_WILDCARD = PermissionCalculator.SUB_WILDCARD;

    public static void injectPermissible(Player player, PlayerData data) {
        UUID uuid = player.getUniqueId();
        PermissibleBase old = player.getPermissibleBase();
        PermissibleBase newBase = new OctoPermissible(player, old);
        player.setPermissibleBase(newBase);
        if (player.getPermissibleBase() instanceof OctoPermissible) {
            Logger.debug("Successfully injected permissible!");
            data.loadPerms(player);
        } else {
            Logger.error("Could not inject permissible!");
            PlayerManager.captureSentryEvent("Could not inject permissible!", player);
        }
    }

    public static boolean isWildcard(String perm) {
        return perm.equalsIgnoreCase(ROOT_WILDCARD) || perm.endsWith(SUB_WILDCARD);
    }

    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }

}
