package net.octopvp.octocore.common;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.ServerInfo;
import net.octopvp.octocore.common.redis.RedisHandler;

@Getter
public class OctoCoreCommon {
    private static ServerInfo info;
    @Setter
    @Getter
    private static RedisHandler redisHandler;
    @Getter
    @Setter
    private static Gson gson;
    @Getter
    @Setter
    private static ClassLoader pluginClassLoader;
    @Getter
    @Setter
    private static boolean disabling = false;

    public static void init(ServerInfo info) {
        OctoCoreCommon.info = info;
    }

    public static String getServerName() {
        return info.getServerName();
    }
}
