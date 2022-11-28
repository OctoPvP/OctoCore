package net.octopvp.octocore.core.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerCloseInventoryEvent extends PlayerEvent {
    private static HandlerList handlerList;

    static {
        PlayerCloseInventoryEvent.handlerList = new HandlerList();
    }

    public PlayerCloseInventoryEvent(final Player player) {
        super(player);
    }

    public static HandlerList getHandlerList() {
        return PlayerCloseInventoryEvent.handlerList;
    }

    public HandlerList getHandlers() {
        return PlayerCloseInventoryEvent.handlerList;
    }
}
