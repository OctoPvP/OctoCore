package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.autoinit.SongManager;
import net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.RadioSongPlayer;
import net.octopvp.octocore.paper.utils.Sender;

public class StopSongCommand extends BaseCommand {
    @Command(name = "stopsong")
    public CommandResult execute(Sender sender, String[] args) {
        RadioSongPlayer rsp = SongManager.getSongPlayerMap().get(sender.getPlayer().getUniqueId());
        rsp.setPlaying(false);
        SongManager.getSongPlayerMap().remove(sender.getPlayer().getUniqueId());
        return CommandResult.SUCCESS;
    }
}
