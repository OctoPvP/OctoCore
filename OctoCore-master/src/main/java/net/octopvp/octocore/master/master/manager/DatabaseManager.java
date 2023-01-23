package net.octopvp.octocore.master.master.manager;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.common.interfaces.manager.IDatabaseManager;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.master.master.OctoCoreMaster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Component
@Getter
public class DatabaseManager implements IDatabaseManager {
    private static MongoDatabase mongoDatabase = null;
    private static MongoClient mongoClient;

    @Value("${master.mongo.host}")
    private String mongoHost;
    @Value("${master.mongo.port}")
    private int mongoPort;
    @Value("${master.mongo.database}")
    private String mongoDatabaseName;
    @Value("${master.mongo.auth.user}")
    private String mongoUsername;
    @Value("${master.mongo.auth.password}")
    private String mongoPassword;
    @Value("${master.mongo.auth.db}")
    private String mongoAuthDB;
    @Value("${master.mongo.auth.enabled}")
    private boolean mongoAuth;

    @PostConstruct
    public void init() {
        /*
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
         */
        MongoCredential credential;
        if (mongoAuth) {
            credential = MongoCredential.createCredential(mongoUsername, mongoAuthDB, mongoPassword.toCharArray());
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Collections.singletonList(new ServerAddress(mongoHost, mongoPort))))
                            .credential(credential)
                            .build());
        } else {
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Collections.singletonList(new ServerAddress(mongoHost, mongoPort))))
                            .build());
        }
        mongoDatabase = mongoClient.getDatabase(mongoDatabaseName);
    }

    @Override
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    @Override
    public RedisManager getRedisManager() {
        return OctoCoreMaster.getRedisManager();
    }

    @Override
    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }
}
