package net.octopvp.octocore.paper.manager.impl;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishmentModule;
import net.octopvp.octocore.paper.utils.Logger;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

public class DatabaseManager extends Manager {
    private static MongoDatabase mongoDatabase = null;
    private static MongoClient mongoClient;

    public static MongoDatabase getMongoDatabase() {
        return DatabaseManager.mongoDatabase;
    }

    public static MongoClient getMongoClient() {
        return DatabaseManager.mongoClient;
    }
    //private RedisManager redisManager;

    @Override
    public void init(OctoCore plugin) {
        MongoCredential credentials;
        Logger.info("Connecting to mongo");
        String base = "database.mongo.auth.";
        if(plugin.getConfig().getBoolean(base + "enabled")){
            credentials = MongoCredential.createCredential(plugin.getConfig().getString(base + "username"),plugin.getConfig().getString(base + "db"),plugin.getConfig().getString(base + "password").toCharArray());
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Arrays.asList(new ServerAddress(plugin.getConfig().getString("database.mongo.host"), plugin.getConfig().getInt("database.mongo.port")))))
                            .credential(credentials)
                            .build());
        }
        else {
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Arrays.asList(new ServerAddress(plugin.getConfig().getString("database.mongo.host"), plugin.getConfig().getInt("database.mongo.port")))))
                            .build());
        }
        mongoDatabase = mongoClient.getDatabase("OctoCore");
        Logger.info(mongoDatabase == null ? "Could not connect to mongo!" : "Connected to mongo!");
        //redisManager = new RedisManager();
        PlayerManager.postDBInit();
        new PunishmentModule().onEnable(plugin);
    }

    public static Jedis getJedis(){
        return OctoCore.getInstance().getRedisData().getPool().getResource();
    }



    @Override
    public void disable() {
        //redisManager.disable();
    }
}
