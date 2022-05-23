package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.OctoCore;
import org.bukkit.Bukkit;

import java.util.List;

public class SetupPermissions implements Setup {

    @Override
    public void setup(OctoCore plugin) {
        Permissions[] permissions = Permissions.values();
        List<String> perms = Bukkit.getPluginManager().getPermissionsString();
        for (Permissions permission : permissions) {
            String node = permission.getNode();
            if (node == "") {
                continue;
            }
            if (perms.contains(node)) {
                //Logger.debug("Not registering permission \"" + node + "\" because it already is.");
                continue;
            }
            //Logger.debug("Registering permission \"" + node + "\"");
            Bukkit.getPluginManager().getPermissions().add(new org.bukkit.permissions.Permission(node));
        }
    }

    @Override
    public void disable(OctoCore plugin) {

    }

}
