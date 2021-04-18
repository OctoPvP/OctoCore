package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.permission.Permission;
import net.octopvp.octocore.paper.utils.reflection.ReflectUtil;
import org.bukkit.Bukkit;

import java.util.List;

public class SetupPermissions implements Setup{

    @Override
    public void setup(OctoCorePaper plugin) {
        try {
            Permission[] permissions = ReflectUtil.getEnumValues(Permission.class);
            List<String> perms = Bukkit.getPluginManager().getPermissionsString();
            for (Permission permission : permissions) {
                if(perms.contains(permission.getNode())) {
                    Logger.debug("Not registering permission \"" + permission.getNode() + "\" because it already is.");
                    continue;
                }
                Logger.debug("Registering permission \"" + permission.getNode() + "\"");
                Bukkit.getPluginManager().getPermissions().add(new org.bukkit.permissions.Permission(permission.getNode()));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disable(OctoCorePaper plugin) {

    }

}
