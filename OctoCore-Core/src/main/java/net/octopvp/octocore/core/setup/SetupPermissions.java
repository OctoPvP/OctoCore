package net.octopvp.octocore.core.setup;

import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SetupPermissions implements Setup {

    @Override
    public void setup(OctoCore plugin) {
        List<String> perms = Bukkit.getPluginManager().getPermissions().stream().map(org.bukkit.permissions.Permission::getName).collect(Collectors.toList());
        for (Field declaredField : Permissions.class.getDeclaredFields()) {
            if (declaredField.getType() != String.class) continue;
            String node = null;
            try {
                node = (String) declaredField.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (Objects.equals(node, "")) {
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
