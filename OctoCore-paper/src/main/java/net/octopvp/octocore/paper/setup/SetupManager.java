package net.octopvp.octocore.paper.setup;

import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.utils.ReflectionUtils;
import org.bukkit.Material;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

@Getter
public class SetupManager implements Setup {

    private ArrayList<Manager> managers = new ArrayList<>();

    public static SetupManager instance;

    @Override
    public void setup(OctoCore plugin) {
        instance = this;

        for (Field field : OctoCore.getInstance().getClass().getDeclaredFields()) {
            if (Manager.class.isAssignableFrom(field.getType()) && field.getType().getSuperclass() == Manager.class) {
                field.setAccessible(true);
                try {
                    Constructor constructor = field.getType().getDeclaredConstructor();
                    Object o = constructor.newInstance();
                    field.set(OctoCore.getInstance(), o);
                    managers.add((Manager) o);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Class<?> clazz : ReflectionUtils.getClassesInPackage(plugin, "net.octopvp.octocore.paper.manager.impl.autoinit")) {
            try {
                Object o = clazz.getConstructor().newInstance();
                managers.add((Manager) o);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        /*
        for (Manager manager : managers) {
            
        }
         */
        //managers.forEach(manager -> manager.init(plugin));
    }

    @Override
    public void disable(OctoCore plugin) {
        managers.forEach(Manager::disable);
    }
}
