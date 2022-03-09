package net.octopvp.octocore.paper.command.impl;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public class GetUUID extends BaseCommand {
    @Command(name = "getuuid")
    public CommandResult execute(Sender sender, String[] args) {
        BaseComponent component = new ComponentBuilder(sender.getUniqueId().toString()).event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, sender.getUniqueId().toString())).getComponent(0);
        sender.getPlayer().sendMessage(component);
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}
