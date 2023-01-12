package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.core.manager.impl.RankManager;

import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class RankProvider implements Provider<Rank> {
    @Override
    public Rank provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        Rank rank = RankManager.getInstance().getRankByName(args.pop());
        if (rank == null) throw new CommandException("Could not find that rank!");
        return rank;
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return RankManager.getRanks().stream().map(Rank::getName).collect(Collectors.toList());
    }
}
