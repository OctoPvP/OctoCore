package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.bukkit.providers.PlayerProvider;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;
import java.util.UUID;

public class OnlinePlayerProvider implements Provider<OnlinePlayer> {
    @Override
    public OnlinePlayer provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (commandInfo.getCommander().getPlatform().isSenderParameter(parameterInfo))
            return OctoCore.getInstance().getServerManager().getOnlinePlayer((UUID) context.getCommandSender().getIdentifier());
        return OctoCore.getInstance().getServerManager().getOnlinePlayer(args.pop());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return PlayerProvider.suggest(sender);
    }
}
