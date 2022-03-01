package net.octopvp.octocore.paper.listeners.redis;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.paper.objects.RedisHandler;

import java.util.HashSet;
import java.util.Set;

public class MainRedisHandler {
    @Getter
    private static final Set saving = new HashSet();

}
