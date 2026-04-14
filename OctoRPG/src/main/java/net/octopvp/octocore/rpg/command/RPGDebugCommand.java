package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RPGDebugCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!(stack.getSender() instanceof Player player)) {
            stack.getSender().sendMessage("This command can only be used by players.");
            return;
        }
        if (!player.hasPermission("octorpg.admin")) {
            player.sendMessage(CC.RED + "No permission.");
            return;
        }
        
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player);
        if (data != null) {
            data.setDebug(!data.isDebug());
            player.sendMessage(CC.translate("&7[&bRPG Debug&7] &fDebug mode " + (data.isDebug() ? "&aEnabled" : "&cDisabled")));
        }
    }
}
