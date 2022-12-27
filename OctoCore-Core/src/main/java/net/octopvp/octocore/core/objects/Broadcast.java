package net.octopvp.octocore.core.objects;

import lombok.Getter;
import net.octopvp.octocore.core.database.redis.packets.server.GlobalBroadcastPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Broadcast {
    private static final HashMap<UUID, Broadcast> broadcastIds = new HashMap<>();
    private final UUID broadcastId = UUID.randomUUID();
    private final Map<String, Integer> responses = new ConcurrentHashMap<>();
    private String message;
    private UUID player;

    public Broadcast(String s, UUID player) {
        this.player = player;
        this.message = s;
        broadcastIds.put(broadcastId, this);
    }

    public static Broadcast getBroadcast(UUID id) {
        return broadcastIds.get(id);
    }

    //builders are cool :D
    public Broadcast send() {
        new GlobalBroadcastPacket(message).send();
        return this;
    }

    public Broadcast setMessage(String message) {
        this.message = message;
        return this;
    }

    public Broadcast setPlayer(UUID player) {
        this.player = player;
        return this;
    }
}
