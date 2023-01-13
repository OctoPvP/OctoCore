package net.octopvp.octocore.common.manager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import net.octopvp.octocore.common.redis.RedisManager;

public interface IDatabaseManager {
    MongoClient getMongoClient();

    RedisManager getRedisManager();

    MongoDatabase getDatabase();
}
