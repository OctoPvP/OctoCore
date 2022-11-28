package net.octopvp.octocore.core.module.impl.noteblockapi.songplayer;

import net.octopvp.octocore.core.module.impl.noteblockapi.model.Playlist;
import net.octopvp.octocore.core.module.impl.noteblockapi.model.Song;
import net.octopvp.octocore.core.module.impl.noteblockapi.model.SoundCategory;
import org.bukkit.entity.Player;

/**
 * SongPlayer playing only in specified distance
 */
public abstract class RangeSongPlayer extends SongPlayer {

    private int distance = 16;

    public RangeSongPlayer(Song song, SoundCategory soundCategory) {
        super(song, soundCategory);
    }

    public RangeSongPlayer(Song song) {
        super(song);
    }

    protected RangeSongPlayer(net.octopvp.octocore.core.module.impl.noteblockapi.SongPlayer songPlayer) {
        super(songPlayer);
    }

    public RangeSongPlayer(Playlist playlist, SoundCategory soundCategory) {
        super(playlist, soundCategory);
    }

    public RangeSongPlayer(Playlist playlist) {
        super(playlist);
    }

    @Override
    void update(String key, Object value) {
        super.update(key, value);

        if ("distance".equals(key)) {
            distance = (int) value;
        }
    }

    public int getDistance() {
        return distance;
    }

    /**
     * Sets distance in blocks where would be player able to hear sound.
     *
     * @param distance (Default 16 blocks)
     */
    public void setDistance(int distance) {
        this.distance = distance;
        CallUpdate("distance", distance);
    }

    /**
     * Returns true if the Player is able to hear the current RangeSongPlayer
     *
     * @param player in range
     * @return ability to hear the current RangeSongPlayer
     */
    public abstract boolean isInRange(Player player);

}
