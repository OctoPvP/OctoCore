package net.octopvp.octocore.paper.manager.impl;

import com.google.gson.JsonObject;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.RedisData;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.database.redis.object.JedisSettings;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.utils.Logger;

public class RedisManager extends Manager {
    //Load after db manager start
    @Override
    public void init(OctoCore plugin) {
        Logger.debug("Connecting to Redis");
        JedisSettings jedisSettings = new JedisSettings();
        jedisSettings.setAddress(getConfig().getString("database.redis.host"));
        jedisSettings.setPort(getConfig().getInt("database.redis.port"));
        if(getConfig().getBoolean("database.redis.auth")) {
            jedisSettings.setAuth(true);
            jedisSettings.setPassword(getConfig().getString("database.redis.auth.password"));
        }
        OctoCore.getInstance().setRedisData(new RedisData(jedisSettings));
        if (OctoCore.getInstance().getRedisData() == null)
            Logger.error("Could not connect to redis!");
        else{
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("server",OctoCore.getServerName());
            OctoCore.getInstance().getRedisData().write(JedisAction.SERVER_ONLINE,jsonObject);
        }
    }

    @Override
    public void disable() {
        if (OctoCore.getInstance().getRedisData() != null){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("server",OctoCore.getServerName());
            OctoCore.getInstance().getRedisData().write(JedisAction.SERVER_OFFLINE,jsonObject);
        }
    }
}
