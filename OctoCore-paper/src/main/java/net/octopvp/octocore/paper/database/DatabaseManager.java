package net.octopvp.octocore.paper.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RedisManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import org.bson.json.JsonWriterSettings;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Collections;

public class DatabaseManager extends Manager {
    @Getter
    private static final JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();
    private static MongoDatabase mongoDatabase = null;
    private static MongoClient mongoClient;

    public static MongoDatabase getMongoDatabase() {
        return DatabaseManager.mongoDatabase;
    }

    public static MongoClient getMongoClient() {
        return DatabaseManager.mongoClient;
    }

    public static Jedis getJedis() {
        return RedisManager.getJedis();
    }

    @Override
    public void init(OctoCore plugin) {
        MongoCredential credentials;
        Logger.info("Connecting to mongo");
        String base = "database.mongo.auth.";
        if (plugin.getConfig().getBoolean(base + "enabled")) {
            credentials = MongoCredential.createCredential(plugin.getConfig().getString(base + "username"), plugin.getConfig().getString(base + "db"), plugin.getConfig().getString(base + "password").toCharArray());
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Collections.singletonList(new ServerAddress(plugin.getConfig().getString("database.mongo.host"), plugin.getConfig().getInt("database.mongo.port")))))
                            .credential(credentials)
                            .build());
        } else {
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Collections.singletonList(new ServerAddress(plugin.getConfig().getString("database.mongo.host"), plugin.getConfig().getInt("database.mongo.port")))))
                            .build());
        }
        mongoDatabase = mongoClient.getDatabase("OctoCore");
        Logger.info(mongoDatabase == null ? "Could not connect to mongo!" : "Connected to mongo!");
        //redisManager = new RedisManager();
        if (mongoDatabase == null) {
            return;
        }
        PlayerManager.getInstance().postDBInit(mongoDatabase);
        PunishModule.postDbInit(mongoDatabase);
    }

    @Override
    public void disable() {
        //redisManager.disable();
    }
}
