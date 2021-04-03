package net.octopvp.octocore.paper.manager;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.utils.Logger;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.UUID;

public class DatabaseManager implements Manager{
    @Getter
    private static MongoDatabase mongoDatabase = null;
    @Getter
    private static MongoClient mongoClient;
    //@Getter
    //private static Jedis redis;
    @Override
    public void init(OctoCorePaper plugin) {
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
        //redis = new Jedis(plugin.getConfig().getString("database.redis.host"),OctoCorePaper.getInstance().getConfig().getInt("database.redis.port"));
    }
    public static boolean doesDocumentExistByUUID(UUID uuid){
        return PlayerManager.getPdataCollection().find(Filters.eq("uuid",uuid.toString())).projection(Projections.excludeId()).limit(1).iterator().hasNext();
    }
}
