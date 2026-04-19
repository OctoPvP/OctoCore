package net.octopvp.octocore.rpg.util;

import com.google.common.reflect.ClassPath;
import lombok.SneakyThrows;
import net.octopvp.octocore.rpg.OctoRPG;

import java.util.ArrayList;
import java.util.List;

public class ClassUtil {
    @SneakyThrows
    public static List<Class<?>> getClassesInPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        for (ClassPath.ClassInfo classInfo : ClassPath.from(OctoRPG.getInstance().getClass().getClassLoader()).getTopLevelClassesRecursive(packageName)) {
            Class<?> aClass = classInfo.load();
            classes.add(aClass);
        }
        return classes;
    }
}
