package net.octopvp.octocore.core.module.impl.noteblockapi;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @deprecated {@link net.octopvp.octocore.core.module.impl.noteblockapi.event.SongEndEvent}
 */
@Deprecated
public class SongEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final SongPlayer song;

    public SongEndEvent(SongPlayer song) {
        this.song = song;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public SongPlayer getSongPlayer() {
        return song;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
