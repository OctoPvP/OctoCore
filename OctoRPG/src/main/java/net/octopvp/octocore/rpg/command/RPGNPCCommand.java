package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.npc.GameNPC;
import net.octopvp.octocore.rpg.npc.NPCType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RPGNPCCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("octorpg.command.npc")) {
            stack.getSender().sendMessage(CC.RED + "No permission.");
            return;
        }

        if (args.length < 2) {
            stack.getSender().sendMessage(CC.RED + "Usage: /rpgnpc <spawn> <typeId>");
            return;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            if (!(stack.getSender() instanceof Player)) {
                stack.getSender().sendMessage(CC.RED + "Only players can use this command.");
                return;
            }
            Player player = (Player) stack.getSender();
            String typeId = args[1];
            NPCType type = OctoRPG.getInstance().getNpcManager().getNPCTypeById(typeId);
            if (type == null) {
                player.sendMessage(CC.RED + "NPC type '" + typeId + "' not found.");
                return;
            }

            String id = UUID.randomUUID().toString();
            OctoRPG.getInstance().getNpcManager().registerNPC(new GameNPC(id, player.getLocation(), type));
            player.sendMessage(CC.GREEN + "Spawned NPC " + type.getName() + " with ID " + id);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("spawn").stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            // Need a way to get all registered type IDs
            // For now just return empty or I can add a method to NPCManager
        }
        return List.of();
    }
}
