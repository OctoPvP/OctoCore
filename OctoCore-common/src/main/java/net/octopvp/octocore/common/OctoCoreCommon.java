package net.octopvp.octocore.common;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.manager.IServerManager;
import net.octopvp.octocore.common.redis.RedisManager;

@Getter
@Setter
public class OctoCoreCommon {
    @Getter
    private static final OctoCoreCommon instance = new OctoCoreCommon();

    public void init(Gson gson, ServerImplementation serverImplementation) {
        this.gson = gson;
        this.serverImplementation = serverImplementation;
    }

    private RedisManager redisManager;
    private ServerImplementation serverImplementation;
    private Gson gson;
    private boolean bungee = false;

    public IServerManager getServerManager() {
        return serverImplementation.getServerManager();
    }

    public String getServerName() {
        return getServerImplementation().getServerName();
    }
}
