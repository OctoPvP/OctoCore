package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.bukkit.providers.PlayerProvider;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.core.utils.OfflineHelpers;

import java.util.Deque;
import java.util.List;

public class OfflineInfoProvider implements Provider<OfflineHelpers.OfflineInfo> {
    @Override
    public OfflineHelpers.OfflineInfo provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return OfflineHelpers.getOfflineInfo(args.pop());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return PlayerProvider.suggest(sender);
    }
}
