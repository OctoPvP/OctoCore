package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.autoinit.SongManager;
import net.octopvp.octocore.core.module.impl.noteblockapi.model.Song;
import net.octopvp.octocore.core.module.impl.noteblockapi.songplayer.RadioSongPlayer;
import net.octopvp.octocore.core.module.impl.noteblockapi.utils.NBSDecoder;
import net.octopvp.octocore.core.utils.Sender;

import java.io.File;

public class PlaySongCommand {
    @Command(name = "playsong", aliases = {"playnbssong"})
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(Sender sender, String fileName) {
        File file = new File(OctoCore.getInstance().getDataFolder().getAbsolutePath() + "/nbs/" + fileName);
        RadioSongPlayer alreadyPlaying = SongManager.getSongPlayerMap().get(sender.getPlayer().getUniqueId());
        if (alreadyPlaying != null)
            alreadyPlaying.setPlaying(false);
        SongManager.getSongPlayerMap().remove(sender.getPlayer().getUniqueId());
        Song song = NBSDecoder.parse(file);
        RadioSongPlayer rsp = new RadioSongPlayer(song);
        rsp.addPlayer(sender.getPlayer());
        rsp.setPlaying(true);
        SongManager.getSongPlayerMap().put(sender.getPlayer().getUniqueId(), rsp);
        return CommandResult.SUCCESS;
    }
}
