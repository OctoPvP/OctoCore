package net.octopvp.octocore.paper.manager.impl;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.RedisHandler;
import net.octopvp.octocore.paper.objects.maps.tri.HashTriMap;
import net.octopvp.octocore.paper.objects.maps.tri.TriMap;
import net.octopvp.octocore.paper.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.javatuples.Triplet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RedisListenerManager extends Manager {
    private static final TriMap<RedisHandler, JedisAction, Method, Object> handlers = new HashTriMap<>();
    @Override
    public void init(OctoCore plugin) {
        for (Class<?> clazz : ReflectionUtils.getClassesInPackage(plugin, "net.octopvp.octocore.paper.listeners.redis")) {
            registerHandler(clazz);
        }
        Logger.debug("Successfully registered %1 handlers.", handlers.size());
    }
    @SneakyThrows
    public static void handleMessage(JedisAction action, JsonObject data){
        for (Triplet<JedisAction, Method,Object> applicableHandler : getApplicableHandlers(action)) {
            applicableHandler.getValue1().invoke(applicableHandler.getValue2(),data);
        }
    }
    public static Triplet<JedisAction,Method,Object>[] getApplicableHandlers(JedisAction action){
        List<Triplet<JedisAction,Method,Object>> ret = new ArrayList<>();
        handlers.forEach(((redisHandler, action1, method,obj) -> {
            if (action1 == action)
                ret.add(new Triplet<>(action1,method,obj));
        }));
        return ret.toArray(new Triplet[0]);
    }
    @SneakyThrows
    public void registerHandler(Class clazz){
        Object obj = clazz.newInstance();
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(RedisHandler.class)){
                if ((declaredMethod.getParameterTypes().length != 1) || declaredMethod.getParameterTypes()[0] != JsonObject.class){
                    Bukkit.getLogger().severe("Could not register handler: " + declaredMethod.getName() + ". Unexpected method arguments.");
                    continue;
                }
                RedisHandler handlerAnnotation = declaredMethod.getAnnotation(RedisHandler.class);
                handlers.put(handlerAnnotation, handlerAnnotation.jedisAction(),declaredMethod,obj);
            }
        }
    }

    @Override
    public void disable() {

    }
}
