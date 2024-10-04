package net.octopvp.octocore.v1_21.listener;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import net.kyori.adventure.text.Component;
import net.octopvp.octocore.core.manager.impl.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

public class VanishListener_1_21 implements Listener {
    @EventHandler
    public void onMobLook(EntityPathfindEvent event) { // TODO backport this event to 1.8
        if (event.getEntity() instanceof Player player) {
            if (VanishManager.getInstance().isVanished(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        if (VanishManager.getInstance().isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) { // TODO backport this event to 1.8
        if (VanishManager.getInstance().isVanished(event.getPlayer())) {
            Component oldMessage = event.message();
            if (oldMessage != null) {

                event.getPlayer().sendMessage(Component.text("You got an achievement but you're vanished so it was muted!\n")
                        .append(Component.text("[SILENT] ").append(oldMessage)));
            }
            event.message(null);
        }
    }
}
