package net.octopvp.octocore.paper.listeners.redis;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.listeners.JoinLeaveListener;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.RedisHandler;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class MainRedisHandler {
    @Getter
    private static Set saving = new HashSet();
    @RedisHandler(jedisAction = JedisAction.SAVE_REQUEST_SWITCH)
    public void onSaveReq(JsonObject data){

    }
}
