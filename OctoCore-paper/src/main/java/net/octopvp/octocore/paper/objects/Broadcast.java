package net.octopvp.octocore.paper.objects;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Broadcast {
    private String message;
    private UUID player;
    private static HashMap<UUID,Broadcast> broadcastIds = new HashMap<>();
    private UUID broadcastId = UUID.randomUUID();
    private Map<String,Integer> responses = new ConcurrentHashMap<>();
    public Broadcast(String s,UUID player){
        this.player = player;
        this.message = s;
        broadcastIds.put(broadcastId,this);
    }
    //builders are cool :D
    public Broadcast send(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message",message);
        jsonObject.addProperty("origin", OctoCore.getServerName());
        jsonObject.addProperty("id",broadcastId.toString());
        if (player != null){
            jsonObject.addProperty("player",true);
        }
        OctoCore.getInstance().getRedisData().write(JedisAction.GLOBAL_BROADCAST,jsonObject);
        Tasks.runLater(()->{
            AtomicInteger players = new AtomicInteger();
            AtomicInteger servers = new AtomicInteger();
            responses.forEach((k,v)->{
                players.set(players.get() + v);
                servers.getAndIncrement();
            });
            String message = Lang.BROADCAST_RESPONSE.getMsg(players.get(),servers.get());
            if (player != null){
                Player p = Bukkit.getPlayer(player);
                if (p != null)
                    p.sendMessage(message);
            }
            Logger.info(message);
        },15l);
        return this;
    }
    public Broadcast setMessage(String message){
        this.message = message;
        return this;
    }
    public Broadcast setPlayer(UUID player){
        this.player = player;
        return this;
    }
    public static Broadcast getBroadcast(UUID id){
        return broadcastIds.get(id);
    }
}
