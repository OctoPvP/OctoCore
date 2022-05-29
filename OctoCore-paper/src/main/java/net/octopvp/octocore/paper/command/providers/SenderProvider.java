package net.octopvp.octocore.paper.command.providers;

import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.Deque;
import java.util.List;

public class SenderProvider implements Provider<Sender> {
    @Override
    public Sender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        BukkitCommandSender sender = (BukkitCommandSender) context.getCommandSender();
        return new Sender(sender.getSender());
    }

    @Override
    public List<String> provideSuggestions(String input, CoreCommandSender sender) {
        return null;
    }
}
