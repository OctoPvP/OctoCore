package net.octopvp.octocore.paper.command.impl;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import org.bukkit.entity.Player;

public class GetUUID {
    @Command(name = "getuuid")
    public void execute(@Sender Player sender) {
        BaseComponent component = new ComponentBuilder(sender.getUniqueId().toString()).event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, sender.getUniqueId().toString())).getComponent(0);
        sender.getPlayer().sendMessage(component);
    }
}
