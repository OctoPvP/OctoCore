package net.octopvp.octocore.paper.manager;

import net.octopvp.octocore.paper.OctoCorePaper;
import redis.clients.jedis.Jedis;

public class DatabaseManager implements Manager{
    @Override
    public void init(OctoCorePaper plugin) {
        Jedis redis = new Jedis(OctoCorePaper.getInstance().getConfig().getString("database.redis.host"),OctoCorePaper.getInstance().getConfig().getInt("database.redis.port"));
    }
}
