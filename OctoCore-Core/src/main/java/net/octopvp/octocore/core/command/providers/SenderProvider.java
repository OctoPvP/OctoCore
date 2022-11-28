package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.utils.Sender;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;

public class SenderProvider implements Provider<Sender> {
    @Override
    public Sender provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        BukkitCommandSender sender = (BukkitCommandSender) context.getCommandSender();
        return new Sender(sender.getSender());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return OctoCore.getInstance().getCommander().getArgumentProviders().get(Player.class).provideSuggestions(input, lastArg, sender);
    }

}
