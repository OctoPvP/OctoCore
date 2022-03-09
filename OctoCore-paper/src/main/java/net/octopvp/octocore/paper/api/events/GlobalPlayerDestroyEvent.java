package net.octopvp.octocore.paper.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player leave network, called if player has 2 seconds of inactivity.
 */
@RequiredArgsConstructor
@Getter
public class GlobalPlayerDestroyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final GlobalPlayer globalPlayer;

    private boolean cancelled;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
