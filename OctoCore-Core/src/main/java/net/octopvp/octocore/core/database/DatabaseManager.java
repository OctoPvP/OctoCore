package net.octopvp.octocore.core.database;

import com.mongodb.Function;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.common.interfaces.manager.IDatabaseManager;
import net.octopvp.octocore.common.mongo.codec.UUIDCodec;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.RedisManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonWriterSettings;
import redis.clients.jedis.Jedis;

import java.util.Collections;

public class DatabaseManager extends Manager implements IDatabaseManager {
    @Getter
    private static final JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();
    private static MongoDatabase mongoDatabase = null;
    private static MongoClient mongoClient;

    public static MongoDatabase getMongoDatabase() {
        return DatabaseManager.mongoDatabase;
    }

    public static Jedis getJedis() {
        return RedisManager.getJedis();
    }

    @Override
    public void init(OctoCore plugin) {
        MongoCredential credentials;
        Logger.info("Connecting to mongo");
        String authBase = "database.mongo.auth.";
        Function<MongoClientSettings.Builder, MongoClientSettings.Builder> mutator = b -> {
            CodecRegistry registry = CodecRegistries.fromCodecs(new UUIDCodec());
            CodecRegistry defaultRegistry = MongoClientSettings.getDefaultCodecRegistry();
            return b.codecRegistry(CodecRegistries.fromRegistries(registry, defaultRegistry));
        };
        if (plugin.getConfig().getBoolean(authBase + "enabled")) {
            credentials = MongoCredential.createCredential(plugin.getConfig().getString(authBase + "username"), plugin.getConfig().getString(authBase + "db"), plugin.getConfig().getString(authBase + "password").toCharArray());
            mongoClient = MongoClients.create(
                    mutator.apply(MongoClientSettings.builder()
                                    .applyToClusterSettings(builder ->
                                            builder.hosts(Collections.singletonList(new ServerAddress(plugin.getConfig().getString("database.mongo.host"), plugin.getConfig().getInt("database.mongo.port")))))
                                    .credential(credentials))
                            .build());
        } else {
            mongoClient = MongoClients.create(
                    mutator.apply(MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Collections.singletonList(new ServerAddress(plugin.getConfig().getString("database.mongo.host"), plugin.getConfig().getInt("database.mongo.port")))))
                    ).build());
        }
        mongoDatabase = mongoClient.getDatabase("OctoCore");
        //redisManager = new RedisManager();
        Logger.info("Connected to mongo!");
        PlayerManager.getInstance().postDBInit(mongoDatabase);
        PunishModule.postDbInit(mongoDatabase);
    }

    @Override
    public void disable() {
        //redisManager.disable();
    }

    @Override
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    @Override
    public net.octopvp.octocore.common.redis.RedisManager getRedisManager() {
        return OctoCore.getInstance().getActualRedisManager();
    }

    @Override
    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }
}
