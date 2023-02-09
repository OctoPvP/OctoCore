package net.octopvp.octocore.common;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.interfaces.ServerImplementation;
import net.octopvp.octocore.common.interfaces.manager.IPunishModule;
import net.octopvp.octocore.common.interfaces.manager.IRankManager;
import net.octopvp.octocore.common.interfaces.manager.IServerManager;
import net.octopvp.octocore.common.redis.RedisManager;

@Getter
@Setter
public class OctoCoreCommon {
    @Getter
    private static final OctoCoreCommon instance = new OctoCoreCommon();
    private MongoClient mongoClient;
    private ServerImplementation serverImplementation;
    private Gson gson;
    private boolean bungee = false;

    public void init(Gson gson, ServerImplementation serverImplementation) {
        this.gson = gson;
        this.serverImplementation = serverImplementation;
        System.out.println("Git Commit: " + serverImplementation.getBuildGetter().getCommit());
    }

    public IServerManager getServerManager() {
        return serverImplementation.getServerManager();
    }

    public String getServerName() {
        return getServerImplementation().getServerName();
    }

    public IRankManager getRankManager() {
        return serverImplementation.getRankManager();
    }

    public IPunishModule getPunishModule() {
        return serverImplementation.getPunishModule();
    }

    public RedisManager getRedisManager() {
        return serverImplementation.getDatabaseManager().getRedisManager();
    }
}
