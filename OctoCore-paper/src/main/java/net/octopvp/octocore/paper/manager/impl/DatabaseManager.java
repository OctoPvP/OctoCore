package net.octopvp.octocore.paper.manager.impl;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.utils.Logger;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.UUID;

public class DatabaseManager implements Manager {
    private static MongoDatabase mongoDatabase = null;
    private static MongoClient mongoClient;

    public static MongoDatabase getMongoDatabase() {
        return DatabaseManager.mongoDatabase;
    }

    public static MongoClient getMongoClient() {
        return DatabaseManager.mongoClient;
    }

    @Getter
    private static Jedis redis = null;
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
        PlayerManager.postDBInit();
        redis = new Jedis(plugin.getConfig().getString("database.redis.host"), OctoCore.getInstance().getConfig().getInt("database.redis.port"));
        if(OctoCore.getInstance().getConfig().getBoolean("database.redis.auth"))
            redis.auth(OctoCore.getInstance().getConfig().getString("database.redis.auth.password"));
    }

    @Override
    public void disable(OctoCore plugin) {}

}
