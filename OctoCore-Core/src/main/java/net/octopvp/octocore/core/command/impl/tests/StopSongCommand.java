package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.autoinit.SongManager;
import net.octopvp.octocore.core.module.impl.noteblockapi.songplayer.RadioSongPlayer;
import net.octopvp.octocore.core.utils.Sender;

public class StopSongCommand {
    @Command(name = "stopsong")
    @Permission(Permissions.ADMIN)
    public CommandResult execute(Sender sender) {
        RadioSongPlayer rsp = SongManager.getSongPlayerMap().get(sender.getPlayer().getUniqueId());
        rsp.setPlaying(false);
        SongManager.getSongPlayerMap().remove(sender.getPlayer().getUniqueId());
        return CommandResult.SUCCESS;
    }
}
