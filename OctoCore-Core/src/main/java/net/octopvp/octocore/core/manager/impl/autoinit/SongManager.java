package net.octopvp.octocore.core.manager.impl.autoinit;

import lombok.Getter;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.module.impl.noteblockapi.songplayer.RadioSongPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SongManager extends Manager {
    @Getter
    private static final Map<UUID, RadioSongPlayer> songPlayerMap = new ConcurrentHashMap<>();

    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
}
