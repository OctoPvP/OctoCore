package net.octopvp.octocore.paper.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class SimpleEvent extends Event {
    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }
}
