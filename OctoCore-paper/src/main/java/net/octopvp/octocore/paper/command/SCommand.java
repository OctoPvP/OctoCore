package net.octopvp.octocore.paper.command;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public abstract class SCommand {
    public OctoCore plugin = OctoCore.getInstance();

    public abstract CommandResult execute(Sender sender, String[] args);

    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}
