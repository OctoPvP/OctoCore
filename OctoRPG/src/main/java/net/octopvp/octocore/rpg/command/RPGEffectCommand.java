package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RPGEffectCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("octorpg.admin")) {
            stack.getSender().sendMessage(CC.RED + "No permission.");
            return;
        }

        if (args.length < 2) {
            stack.getSender().sendMessage(CC.RED + "Usage: /rpgeffect <player> <effect> [seconds]");
            stack.getSender().sendMessage(CC.RED + "Effects: blight, stun");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            stack.getSender().sendMessage(CC.RED + "Player not found.");
            return;
        }

        RPGPlayerData data = RPGPlayerManager.getInstance().getData(target);
        if (data == null) {
            stack.getSender().sendMessage(CC.RED + "Player data not found.");
            return;
        }

        String effect = args[1].toLowerCase();
        int seconds = 10;
        if (args.length > 2) {
            try {
                seconds = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {}
        }

        switch (effect) {
            case "blight" -> {
                data.applyBlight(seconds);
                stack.getSender().sendMessage(CC.GREEN + "Applied BLIGHT to " + target.getName() + " for " + seconds + " seconds.");
                if (stack.getSender() instanceof Player p) {
                    RPGPlayerData senderData = RPGPlayerManager.getInstance().getData(p);
                    if (senderData != null && senderData.isDebug()) {
                        p.sendMessage(CC.translate("&7[&bRPG Debug&7] &fCommand Execution: Applied &dBLIGHT &fto &d" + target.getName()));
                    }
                }
            }
            case "stun" -> {
                data.setStunned(seconds * 10); // Stun is currently in half-seconds/ticks in your code logic
                stack.getSender().sendMessage(CC.GREEN + "Applied STUN to " + target.getName() + " for " + seconds + " seconds.");
                if (stack.getSender() instanceof Player p) {
                    RPGPlayerData senderData = RPGPlayerManager.getInstance().getData(p);
                    if (senderData != null && senderData.isDebug()) {
                        p.sendMessage(CC.translate("&7[&bRPG Debug&7] &fCommand Execution: Applied &eSTUN &fto &d" + target.getName()));
                    }
                }
            }
            default -> {
                stack.getSender().sendMessage(CC.RED + "Unknown effect: " + effect);
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return List.of("blight", "stun").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
