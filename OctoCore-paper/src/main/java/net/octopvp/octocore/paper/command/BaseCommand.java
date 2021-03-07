package net.octopvp.octocore.paper.command;

import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public interface BaseCommand {
    public OctoCorePaper plugin = OctoCorePaper.getInstance();
    CommandResult execute(Sender sender,String[] args);
    List<String> tabComplete(Sender sender, String[] args);
}
