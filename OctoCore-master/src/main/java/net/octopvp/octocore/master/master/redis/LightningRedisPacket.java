package net.octopvp.octocore.master.master.redis;

import com.google.gson.JsonObject;
import net.badbird5907.lightning.event.Cancellable;
import net.badbird5907.lightning.event.Event;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.master.component.LightningHolder;

public abstract class LightningRedisPacket extends RedisPacket implements Event, Cancellable {
    private transient boolean cancelled = false;

    @Override
    public final void onReceive(JsonObject data) {
        Logger.debug("Received packet " + this.getClass().getSimpleName());
        LightningHolder.getInstance().lightning().call(this);
        if (!isCancelled()) receive(data);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public abstract void receive(JsonObject data);
}
