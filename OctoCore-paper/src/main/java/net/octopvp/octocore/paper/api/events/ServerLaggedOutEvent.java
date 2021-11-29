package net.octopvp.octocore.paper.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerLaggedOutEvent extends Event {
    private static HandlerList handlerList;
    private int averagePing;

    public ServerLaggedOutEvent(final int averagePing) {
        super(true);
        this.averagePing = averagePing;
    }

    public HandlerList getHandlers() {
        return ServerLaggedOutEvent.handlerList;
    }

    public static HandlerList getHandlerList() {
        return ServerLaggedOutEvent.handlerList;
    }

    static {
        ServerLaggedOutEvent.handlerList = new HandlerList();
    }
}
