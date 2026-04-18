package net.octopvp.octocore.rpg.listener;

import eu.decentsoftware.holograms.api.DHAPI;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import net.octopvp.octocore.rpg.util.StatCalculator;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class DamageListener implements Listener {

    public DamageListener(OctoRPG plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeathMessage(PlayerDeathEvent e) {
        Player player = e.getEntity();
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player.getUniqueId());
        if (data != null && data.hasBlight()) {
            net.kyori.adventure.text.Component current = e.deathMessage();
            if (current != null) {
                e.deathMessage(current.append(net.kyori.adventure.text.Component.text("\n")
                        .append(net.kyori.adventure.text.Component.text("They were consumed by the Blight. Their soul withered into nothingness.")
                                .color(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE))));
            }
            
            // Critical: Forcefully clear Blight and UI elements to prevent respawn lockout
            data.applyBlight(0);
        }

        // Kick the player after death
        OctoRPG.getInstance().getServer().getScheduler().runTaskLater(OctoRPG.getInstance(), () -> {
            if (player.isOnline()) {
                player.kick(net.kyori.adventure.text.Component.text("You have fallen in battle.")
                        .color(net.kyori.adventure.text.format.NamedTextColor.RED));
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            RPGPlayerData data = RPGPlayerManager.getInstance().getData(player.getUniqueId());
            if (data != null) {
                double newDamage = StatCalculator.calculateDamage(data.getLevel(), data.getStrengthAfterCalc(), e.getDamage());
                e.setDamage(newDamage);
            }

            // Handle Custom Enchantments
            ItemStack item = player.getInventory().getItemInMainHand();
            OctoRPG.getInstance().getEnchantmentManager().handleHit(player, e.getEntity(), e, item);
            OctoRPG.getInstance().getEnchantmentDurabilityManager().handleUsage(item);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageIndicator(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof LivingEntity victim) || e.getEntity() instanceof ArmorStand) {
            return;
        }

        double damage = e.getFinalDamage();
        if (damage <= 0) return;

        Location loc = victim.getEyeLocation().add(
                ThreadLocalRandom.current().nextDouble(-0.5, 0.5),
                ThreadLocalRandom.current().nextDouble(0.2, 0.7),
                ThreadLocalRandom.current().nextDouble(-0.5, 0.5)
        );

        String text = CC.translate("&c" + (int) Math.ceil(damage) + "❤");
        String name = "damage_indicator_" + UUID.randomUUID().toString();
        
        DHAPI.createHologram(name, loc, List.of(text));
        
        // Remove hologram after 1 second (20 ticks)
        OctoRPG.getInstance().getServer().getScheduler().runTaskLater(OctoRPG.getInstance(), () -> {
            DHAPI.removeHologram(name);
        }, 20L);
    }
}
