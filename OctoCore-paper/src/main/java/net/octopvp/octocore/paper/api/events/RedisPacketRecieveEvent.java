package net.octopvp.octocore.paper.api.events;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.paper.api.SimpleEvent;
import org.bukkit.event.Cancellable;

@RequiredArgsConstructor
public class RedisPacketRecieveEvent extends SimpleEvent implements Cancellable {
    @Getter
    private final RedisPacket packet;
    @Getter
    private final JsonObject data;
    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
