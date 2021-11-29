package net.octopvp.octocore.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerOpenInventoryEvent extends PlayerEvent {
    private static HandlerList handlerList;

    public PlayerOpenInventoryEvent(final Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return PlayerOpenInventoryEvent.handlerList;
    }

    public static HandlerList getHandlerList() {
        return PlayerOpenInventoryEvent.handlerList;
    }

    static {
        PlayerOpenInventoryEvent.handlerList = new HandlerList();
    }
}
