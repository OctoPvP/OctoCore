package net.octopvp.octocore.core.setup;

import lombok.Getter;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.utils.ReflectionUtils;

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

        try {
            for (Field field : OctoCore.getInstance().getClass()
                    .getSuperclass() // because OctoCore is abstract
                    .getDeclaredFields()) {
                Logger.debug(" - Checking field " + field.getName());
                try {
                    if (Manager.class.isAssignableFrom(field.getType()) && field.getType().getSuperclass() == Manager.class) {
                        Logger.debug("  - Setting up " + field.getName() + "...");
                        field.setAccessible(true);
                        try {
                            Constructor constructor = field.getType().getDeclaredConstructor();
                            Object o = constructor.newInstance();
                            field.set(OctoCore.getInstance(), o);
                            managers.add((Manager) o);
                            Logger.debug("   - " + field.getName() + " setup!");
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Logger.debug("  - " + field.getName() + " is not a manager!");
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Class<?> clazz : ReflectionUtils.getClassesInPackage(plugin, "net.octopvp.octocore.paper.manager.impl.autoinit")) {
                try {
                    Object o = clazz.getConstructor().newInstance();
                    managers.add((Manager) o);
                } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                         IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
