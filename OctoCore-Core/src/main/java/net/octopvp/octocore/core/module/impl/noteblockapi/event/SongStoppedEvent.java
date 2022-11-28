package net.octopvp.octocore.core.module.impl.noteblockapi.event;

import net.octopvp.octocore.core.module.impl.noteblockapi.songplayer.SongPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * Called whenever a SongPlayer is stopped
 *
 * @see SongPlayer
 */
public class SongStoppedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final SongPlayer songPlayer;

    public SongStoppedEvent(SongPlayer songPlayer) {
        this.songPlayer = songPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns SongPlayer which is now stopped
     *
     * @return SongPlayer
     */
    public SongPlayer getSongPlayer() {
        return songPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}

