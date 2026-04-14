package net.octopvp.octocore.rpg.listener;

import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import net.octopvp.octocore.rpg.util.StatCalculator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    public DamageListener(OctoRPG plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            RPGPlayerData data = RPGPlayerManager.getInstance().getData(player.getUniqueId());
            if (data != null) {
                double newDamage = StatCalculator.calculateDamage(data.getStrengthAfterCalc(), e.getDamage());
                e.setDamage(newDamage);
            }
        }
    }
}
