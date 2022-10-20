package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.redis.RedisHandler;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.RedisPacketRecieveEvent;
import net.octopvp.octocore.paper.database.redis.packets.server.ServerOfflinePacket;
import net.octopvp.octocore.paper.database.redis.packets.server.ServerOnlinePacket;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.utils.ReflectionUtils;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;

public class RedisManager extends Manager {
    public static Jedis getJedis() {
        return getJedis();
    }

    //Load after db manager start
    @Override
    public void init(OctoCore plugin) {
        Logger.debug("Connecting to Redis");
        JedisSettings jedisSettings = new JedisSettings();
        jedisSettings.setAddress(getConfig().getString("database.redis.host"));
        jedisSettings.setPort(getConfig().getInt("database.redis.port"));
        if (getConfig().getBoolean("database.redis.auth")) {
            jedisSettings.setAuth(true);
            jedisSettings.setPassword(getConfig().getString("database.redis.auth.password"));
        }
        OctoCore.getInstance().setRedisHandler(new RedisHandler("net.octopvp.octocore.paper.database.redis.packets", jedisSettings,
                (runnable) -> {
                    Tasks.runAsync(runnable);
                    return null;
                }, (p) -> {
            RedisPacketRecieveEvent event = new RedisPacketRecieveEvent(p.getValue0(), p.getValue1());
            OctoCore.getInstance().getServer().getPluginManager().callEvent(event);
            return !event.isCancelled();
        }, (pack) -> ReflectionUtils.getClassesInPackage(plugin, pack)));
        OctoCore.getInstance().getRedisHandler().connect();
        OctoCoreCommon.setRedisHandler(OctoCore.getInstance().getRedisHandler());
        try {
            OctoCore.getInstance().getRedisHandler().setupPackets();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            e.printStackTrace();
        }
        if (OctoCore.getInstance().getRedisHandler() == null || !OctoCore.getInstance().getRedisHandler().isConnected())
            Logger.error("Could not connect to redis!");
        else {
            new ServerOnlinePacket(OctoCore.getServerName()).send();
        }
    }

    @Override
    public void disable() {
        if (OctoCore.getInstance().getRedisHandler() != null) {
            new ServerOfflinePacket(OctoCore.getServerName()).send();
            OctoCore.getInstance().getRedisHandler().close();
        }
    }
}
