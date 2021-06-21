package net.octopvp.octocore.paper.module.impl.noteblockapi.model.playmode;

import net.octopvp.octocore.paper.module.impl.noteblockapi.model.Layer;
import net.octopvp.octocore.paper.module.impl.noteblockapi.model.Note;
import net.octopvp.octocore.paper.module.impl.noteblockapi.model.Song;
import net.octopvp.octocore.paper.module.impl.noteblockapi.model.SoundCategory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Decides how is {@link Note} played to {@link Player}
 */
public abstract class ChannelMode {

    @Deprecated
    public abstract void play(Player player, Location location, Song song, Layer layer, Note note,
                              SoundCategory soundCategory, float volume, float pitch);

    public abstract void play(Player player, Location location, Song song, Layer layer, Note note,
                              SoundCategory soundCategory, float volume, boolean doTranspose);
}