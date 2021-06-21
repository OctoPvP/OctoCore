package net.octopvp.octocore.paper.manager.impl.autoinit;

import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.RadioSongPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SongManager extends Manager {
    @Getter
    private static Map<UUID, RadioSongPlayer> songPlayerMap = new ConcurrentHashMap<>();
    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
}
