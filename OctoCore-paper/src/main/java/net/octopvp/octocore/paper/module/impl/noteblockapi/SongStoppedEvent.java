package net.octopvp.octocore.paper.module.impl.noteblockapi;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @deprecated {@link net.octopvp.octocore.paper.module.impl.noteblockapi.event.SongStoppedEvent}
 */
@Deprecated
public class SongStoppedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final SongPlayer songPlayer;

    public SongStoppedEvent(SongPlayer songPlayer) {
        this.songPlayer = songPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public SongPlayer getSongPlayer() {
        return songPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
