package net.octopvp.octocore.common.redis;

import lombok.Getter;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;
import net.octopvp.aetheriacore.common.object.redis.packet.RedisPacket;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class RedisListenerManager {
    private static final Objenesis objenesis = new ObjenesisStd();

    @Getter
    private Set<RedisPacket> packets = new HashSet<>();

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
                            AetheriaCoreCommon.getInstance().getServerImplementation().logDebug("No constructor found for " + field.getType().getName() + " attempting to use experimental Objenesis");
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
            Set<Class<? extends RedisPacket>> classes = reflections.getSubTypesOf(RedisPacket.class);
            for (Class<? extends RedisPacket> aClass : classes) {
                try {
                    packets.add(aClass.getDeclaredConstructor().newInstance());
                } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    AetheriaCoreCommon.getInstance().getServerImplementation().logDebug("No constructor found for " + aClass.getName() + " attempting to use experimental Objenesis");
                    ObjectInstantiator<?> instantiator = objenesis.getInstantiatorOf(aClass);
                    RedisPacket packet = (RedisPacket) instantiator.newInstance();
                    packets.add(packet);
                }
            }
        }
        AetheriaCoreCommon.getInstance().getServerImplementation().logDebug("Successfully registered %1 packets", packets.size());
    }
}
