package net.octopvp.octocore.paper.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.GlobalPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when player joins the network, shouldn't be called on server switch bungee.
 */
@Getter
@RequiredArgsConstructor
public class GlobalPlayerCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GlobalPlayer globalPlayer;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
