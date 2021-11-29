package net.octopvp.octocore.paper.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerCloseInventoryEvent extends PlayerEvent {
    private static HandlerList handlerList;

    public PlayerCloseInventoryEvent(final Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return PlayerCloseInventoryEvent.handlerList;
    }

    public static HandlerList getHandlerList() {
        return PlayerCloseInventoryEvent.handlerList;
    }

    static {
        PlayerCloseInventoryEvent.handlerList = new HandlerList();
    }
}
