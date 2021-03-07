package net.octopvp.octocore.paper.utils.msg;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
@Getter
@Setter
public class Chat {
    private Player player;
    public Chat(Player player) {
        this.player = player;
    }
    public static void sendMessage(Player player, String message){
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }
    public static void sendMessage(CommandSender player, String message){
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }
    public static void sendMessage(Sender player, String message){
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }
    public static void sendMessage(Player player, Lang message){
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }
    public static void sendMessage(CommandSender player, Lang message){
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }
    public static void sendMessage(Sender player, Lang message){
        player.sendMessage(PlaceholderManager.replacePlaceholders(message));
    }
}
