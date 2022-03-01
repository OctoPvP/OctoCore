package net.octopvp.octocore.paper.objects;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.database.redis.packets.server.GlobalBroadcastPacket;
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
        new GlobalBroadcastPacket(message).send();
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
