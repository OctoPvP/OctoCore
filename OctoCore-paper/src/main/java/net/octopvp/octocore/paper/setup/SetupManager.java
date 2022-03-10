package net.octopvp.octocore.paper.setup;

import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.utils.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

@Getter
public class SetupManager implements Setup {

    public static SetupManager instance;
    private final ArrayList<Manager> managers = new ArrayList<>();

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
