package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.autoinit.SongManager;
import net.octopvp.octocore.paper.module.impl.noteblockapi.model.Song;
import net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.RadioSongPlayer;
import net.octopvp.octocore.paper.module.impl.noteblockapi.utils.NBSDecoder;
import net.octopvp.octocore.paper.utils.Sender;

import java.io.File;

public class PlaySongCommand {
    @Command(name = "playsong", aliases = {"playnbssong"}, playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 1) {
            File file = new File(OctoCore.getInstance().getDataFolder().getAbsolutePath() + "/nbs/" + args[0]);
            RadioSongPlayer alreadyPlaying = SongManager.getSongPlayerMap().get(sender.getPlayer().getUniqueId());
            if (alreadyPlaying != null)
                alreadyPlaying.setPlaying(false);
            SongManager.getSongPlayerMap().remove(sender.getPlayer().getUniqueId());
            Song song = NBSDecoder.parse(file);
            RadioSongPlayer rsp = new RadioSongPlayer(song);
            rsp.addPlayer(sender.getPlayer());
            rsp.setPlaying(true);
            SongManager.getSongPlayerMap().put(sender.getPlayer().getUniqueId(), rsp);
        }
        return CommandResult.SUCCESS;
    }
}
