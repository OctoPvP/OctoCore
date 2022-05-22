package net.octopvp.octocore.paper.command.providers;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;

import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class PlayerDataProvider implements Provider<PlayerData> {
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}(-|)[0-9a-f]{4}(-|)[0-9a-f]{4}(-|)[0-9a-f]{4}(-|)[0-9a-f]{12}");

    @Override
    public PlayerData provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        String arg = args.pop();
        if (UUID_PATTERN.matcher(arg).matches()) {
            return PlayerManager.getInstance().getData(UUID.fromString(arg));
        } else {
            return PlayerManager.getInstance().getData(arg);
        }
    }

    @Override
    public List<String> provideSuggestions(String input) {
        return null;
    }

    @Override
    public boolean failOnException() {
        return true;
    }

    @Override
    public boolean failOnExceptionIgnoreOptional() {
        return true;
    }
}
