package net.octopvp.octocore.core.module.impl.noteblockapi;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @deprecated {@link net.octopvp.octocore.core.module.impl.noteblockapi.event.PlayerRangeStateChangeEvent}
 */
@Deprecated
public class PlayerRangeStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final SongPlayer song;
    private final Player player;
    private final boolean state;

    public PlayerRangeStateChangeEvent(SongPlayer song, Player player, boolean state) {
        this.song = song;
        this.player = player;
        this.state = state;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public SongPlayer getSongPlayer() {
        return song;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isInRange() {
        return state;
    }

}
