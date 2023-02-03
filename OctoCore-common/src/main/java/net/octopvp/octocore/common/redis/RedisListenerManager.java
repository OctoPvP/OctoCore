package net.octopvp.octocore.common.redis;

import lombok.Getter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.Disable;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.Logger;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

public class RedisListenerManager {
    private static final Objenesis objenesis = new ObjenesisStd();

    @Getter
    private final Set<RedisPacket> packets = new HashSet<>();

    public RedisListenerManager() {

    }

    public void init(String packageName, Object packetsClazz) {
        if (packetsClazz != null) {
            for (Field field : packetsClazz.getClass().getDeclaredFields()) {
                Class<?> superClass = field.getType().getSuperclass();
                if (superClass != null) {
                    if (superClass.equals(RedisPacket.class) || superClass.getSuperclass() != null && superClass.getSuperclass().equals(RedisPacket.class)) {
                        try {
                            packets.add((RedisPacket) field.getType().getDeclaredConstructor().newInstance());
                        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchMethodException e) {
                            OctoCoreCommon.getInstance().getServerImplementation().logDebug("No constructor found for " + field.getType().getName() + " attempting to use experimental Objenesis");
                            ObjectInstantiator<?> instantiator = objenesis.getInstantiatorOf(field.getType());
                            RedisPacket packet = (RedisPacket) instantiator.newInstance();
                            packets.add(packet);
                        }
                    }
                }
            }
        }
        if (packageName != null) {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> classes = reflections.get(SubTypes.of(RedisPacket.class).asClass());//reflections.getSubTypesOf(RedisPacket.class);
            for (Class<?> aClass : classes) {
                if (aClass.isAnnotationPresent(Disable.class)) {
                    Logger.debug("Skipping packet " + aClass.getName() + " due to @Disable");
                    continue;
                }
                if (Modifier.isAbstract(aClass.getModifiers())) {
                    Logger.debug("Skipping packet " + aClass.getName() + " due to being abstract");
                    continue;
                }
                try {
                    packets.add((RedisPacket) aClass.getDeclaredConstructor().newInstance());
                } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    Logger.error("Failed to register packet " + aClass.getName() + " due to an exception: " + e.getMessage());
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    OctoCoreCommon.getInstance().getServerImplementation().logDebug("No constructor found for " + aClass.getName() + " attempting to use experimental Objenesis");
                    ObjectInstantiator<?> instantiator = objenesis.getInstantiatorOf(aClass);
                    RedisPacket packet = (RedisPacket) instantiator.newInstance();
                    packets.add(packet);
                }
            }
        }
        OctoCoreCommon.getInstance().getServerImplementation().logDebug("Successfully registered %1 packets", packets.size());
    }
}
