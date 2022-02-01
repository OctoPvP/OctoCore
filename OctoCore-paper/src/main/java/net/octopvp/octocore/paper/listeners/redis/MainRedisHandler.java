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

    @RedisHandler(jedisAction = JedisAction.SAVE_REQUEST_SWITCH)
    public void onSaveReq(JsonObject data) {

    }

    @RedisHandler(jedisAction = JedisAction.TAG_UPDATE)
    public static void onTagUpdate(JsonObject data) {

    }
}
