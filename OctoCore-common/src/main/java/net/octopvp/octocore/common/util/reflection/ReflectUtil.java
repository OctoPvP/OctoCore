package net.octopvp.octocore.common.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {
    public static <E extends Enum> E[] getEnumValues(Class<E> enumClass)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = enumClass.getDeclaredField("$VALUES");
        //TODO remove
        System.out.println(f);
        System.out.println(Modifier.toString(f.getModifiers()));
        f.setAccessible(true);
        Object o = f.get(null);
        return (E[]) o;
    }
}
