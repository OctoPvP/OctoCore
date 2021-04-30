package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.permission.Permission;
import net.octopvp.octocore.common.util.reflection.ReflectUtil;
import org.bukkit.Bukkit;

import java.util.List;

public class SetupPermissions implements Setup{

    @Override
    public void setup(OctoCore plugin) {
        try {
            Permission[] permissions = ReflectUtil.getEnumValues(Permission.class);
            List<String> perms = Bukkit.getPluginManager().getPermissionsString();
            for (Permission permission : permissions) {
                String node = Permission.valueOf(permission.getNode()).getNode();
                if(perms.contains(permission.getNode())) {
                    Logger.debug("Not registering permission \"" + node + "\" because it already is.");
                    continue;
                }
                if(node == ""){
                    continue;
                }
                Logger.debug("Registering permission \"" + node + "\"");
                Bukkit.getPluginManager().getPermissions().add(new org.bukkit.permissions.Permission(node));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disable(OctoCore plugin) {

    }

}
