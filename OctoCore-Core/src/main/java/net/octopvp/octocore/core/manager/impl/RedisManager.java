package net.octopvp.octocore.core.manager.impl;

import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.DatabaseManager;
import net.octopvp.octocore.core.database.redis.packets.server.ServerOfflinePacket;
import net.octopvp.octocore.core.manager.Manager;
import redis.clients.jedis.Jedis;

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
        net.octopvp.octocore.common.redis.RedisManager redisManager;
        OctoCore.getInstance().setActualRedisManager(redisManager = new net.octopvp.octocore.common.redis.RedisManager(
                jedisSettings.getAddress(), jedisSettings.getPort(), jedisSettings.getPassword(), /*"net.octopvp.octocore.paper.database.redis.packets"*/
                DatabaseManager.class.getPackage().getName() + ".redis.packets"
                , null
        ));
        Logger.debug("Connected to Redis");
        /*
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
         */
    }

    @Override
    public void disable() {
        /*
        if (OctoCore.getInstance().getRedisHandler() != null) {
            new ServerOfflinePacket(OctoCore.getServerName()).send();
            OctoCore.getInstance().getRedisHandler().close();
        }
         */
        if (OctoCore.getInstance().getActualRedisManager() != null) {
            new ServerOfflinePacket(OctoCore.getServerName()).send();
            OctoCore.getInstance().getActualRedisManager().getPool().close();
        }
    }
}
