package net.octopvp.octocore.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.interfaces.ServerImplementation;
import net.octopvp.octocore.common.interfaces.manager.IPunishModule;
import net.octopvp.octocore.common.interfaces.manager.IRankManager;
import net.octopvp.octocore.common.interfaces.manager.IServerManager;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.common.util.json.OptionalTypeAdapter;
import net.octopvp.octocore.common.util.perms.Node;
import net.octopvp.octocore.common.util.perms.NodeAdapter;

import java.text.SimpleDateFormat;

@Getter
@Setter
public class OctoCoreCommon {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    @Getter
    private static final OctoCoreCommon instance = new OctoCoreCommon();
    private MongoClient mongoClient;
    private ServerImplementation serverImplementation;
    private Gson gson;
    private boolean proxy = false;

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


    public static GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting()
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(Node.class, new NodeAdapter())
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY);
    }
}
