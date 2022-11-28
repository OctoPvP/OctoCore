package net.octopvp.octocore.core.utils.msg;

import net.octopvp.octocore.core.manager.impl.PlaceholderManager;
import net.octopvp.octocore.core.utils.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chat {
    private Player player;

    public Chat(Player player) {
        this.player = player;
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }

    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }

    public static void sendMessage(Sender player, String message) {
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }

    public static void sendMessage(Player player, Lang message) {
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }

    public static void sendMessage(CommandSender player, Lang message) {
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }

    public static void sendMessage(Sender player, Lang message) {
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
