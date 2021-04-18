package net.octopvp.octocore.paper.utils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {
    public static <E extends Enum> E[] getEnumValues(Class<E> enumClass)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = enumClass.getDeclaredField("$VALUES");
        System.out.println(f);
        System.out.println(Modifier.toString(f.getModifiers()));
        f.setAccessible(true);
        Object o = f.get(null);
        return (E[]) o;
    }
}
