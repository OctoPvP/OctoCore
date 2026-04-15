package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LifestealEffect implements EnchantmentEffect {
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public String getEnchantmentId() {
        return "lifesteal";
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        if (item == null) return false;
        net.octopvp.octocore.rpg.item.CustomItem customItem = net.octopvp.octocore.rpg.OctoRPG.getInstance().getItemManager().getCustomItem(item);
        if (customItem == null) {
            // Allow on vanilla swords
            String type = item.getType().name();
            return type.endsWith("_SWORD");
        }
        // Check if it's a sword or greatsword
        return customItem.getWeaponType() == net.octopvp.octocore.rpg.object.WeaponType.SWORD;
    }

    @Override
    public void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item, int level) {
        if (!(victim instanceof LivingEntity)) return;

        long now = System.currentTimeMillis();
        long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        
        if (now - last < 1000) return; // 1 second cooldown

        double healAmount = level * 1.0; // 0.5 hearts = 1.0 HP per level
        
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : 20.0;
        
        double newHealth = Math.min(player.getHealth() + healAmount, maxHealth);
        player.setHealth(newHealth);
        
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.1);
        
        cooldowns.put(player.getUniqueId(), now);
    }
}
