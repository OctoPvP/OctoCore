package net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer;

import net.octopvp.octocore.paper.module.impl.noteblockapi.NoteBlockAPI;
import net.octopvp.octocore.paper.module.impl.noteblockapi.model.*;
import net.octopvp.octocore.paper.module.impl.noteblockapi.model.playmode.ChannelMode;
import net.octopvp.octocore.paper.module.impl.noteblockapi.model.playmode.MonoMode;
import net.octopvp.octocore.paper.module.impl.noteblockapi.model.playmode.MonoStereoMode;
import org.bukkit.entity.Player;

/**
 * SongPlayer playing to everyone added to it no matter where he is
 */
public class RadioSongPlayer extends SongPlayer {

    //protected boolean stereo = true;

    public RadioSongPlayer(Song song) {
        super(song);
        makeNewClone(net.octopvp.octocore.paper.module.impl.noteblockapi.RadioSongPlayer.class);
    }

    public RadioSongPlayer(Song song, SoundCategory soundCategory) {
        super(song, soundCategory);
        makeNewClone(net.octopvp.octocore.paper.module.impl.noteblockapi.RadioSongPlayer.class);
    }

    private RadioSongPlayer(net.octopvp.octocore.paper.module.impl.noteblockapi.SongPlayer songPlayer) {
        super(songPlayer);
    }

    public RadioSongPlayer(Playlist playlist, SoundCategory soundCategory) {
        super(playlist, soundCategory);
        makeNewClone(net.octopvp.octocore.paper.module.impl.noteblockapi.RadioSongPlayer.class);
    }

    public RadioSongPlayer(Playlist playlist) {
        super(playlist);
        makeNewClone(net.octopvp.octocore.paper.module.impl.noteblockapi.RadioSongPlayer.class);
    }

    @Override
    public void playTick(Player player, int tick) {
        byte playerVolume = NoteBlockAPI.getPlayerVolume(player);

        for (Layer layer : song.getLayerHashMap().values()) {
            Note note = layer.getNote(tick);
            if (note == null) {
                continue;
            }

            float volume = (layer.getVolume() * (int) this.volume * (int) playerVolume * note.getVelocity()) / 100_00_00_00F;

            channelMode.play(player, player.getEyeLocation(), song, layer, note, soundCategory, volume, !enable10Octave);
        }
    }

    /**
     * Returns if the SongPlayer will play Notes from two sources as stereo
     *
     * @return if is played stereo
     * @deprecated
     */
    @Deprecated
    public boolean isStereo() {
        return !(channelMode instanceof MonoMode);
    }

    /**
     * Sets if the SongPlayer will play Notes from two sources as stereo
     *
     * @param stereo
     * @deprecated
     */
    @Deprecated
    public void setStereo(boolean stereo) {
        channelMode = stereo ? new MonoMode() : new MonoStereoMode();
    }

    /**
     * Sets how will be {@link Note} played to {@link Player} (eg. mono or stereo). Default is {@link MonoMode}.
     *
     * @param mode
     */
    public void setChannelMode(ChannelMode mode) {
        channelMode = mode;
    }
}
