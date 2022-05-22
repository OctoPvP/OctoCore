package net.octopvp.octocore.paper.command.impl.other;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

public class CreditsCommand {
    @Command(name = "credits", playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        //https://github.com/mcardy/CommandFramework/
        sender.sendMessage(CC.SEPARATOR + CC.NL + CC.GREEN + "OctoPvP - Credits");
        sender.sendMessage(CC.AQUA + "Builds:" + CC.NL + CC.GRAY + " - " + CC.GREEN + "OctoPvP Build Team");
        sender.sendMessage(CC.AQUA + "OctoCore:");
        TextComponent t1 = new TextComponent(CC.GRAY + " - " + CC.GREEN + CC.U + "Badbird5907");
        t1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/Badbird_5907"));
        sender.sendMessage(t1);
        TextComponent t2 = new TextComponent(CC.GRAY + " - " + CC.GREEN + CC.U + "CommandFramework");
        t2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/mcardy/CommandFramework/"));
        sender.sendMessage(t2);
        TextComponent t3 = new TextComponent(CC.GRAY + " - " + CC.GREEN + CC.U + "NoteBlockAPI");
        t3.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/noteblockapi.19287/"));
        sender.sendMessage(t3);
        TextComponent t4 = new TextComponent(CC.GRAY + " - " + CC.GREEN + CC.U + "ScoreBoardLib");
        t4.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/scoreboardlib.11884/"));
        sender.sendMessage(t4);
        sender.sendMessage(CC.GRAY + " - " + CC.GREEN + "Various other libraries/plugins");
        sender.sendMessage(CC.SEPARATOR);
        return CommandResult.SUCCESS;
    }
}
