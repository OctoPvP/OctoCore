package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DefenderEffect implements EnchantmentEffect {
    private static final String ARMOR_MODIFIER_NAME = "Defender Enchantment Boost";
    
    @Override
    public String getEnchantmentId() {
        return "defender";
    }

    @Override
    public boolean isCompatible(ItemStack item) {
        if (item == null) return false;
        String name = item.getType().name();
        return name.endsWith("_SWORD") || name.endsWith("_AXE");
    }

    @Override
    public void onAbilityUse(Player player, String abilityId, ItemStack item, int level) {
        if (abilityId.equalsIgnoreCase("parry") || abilityId.equalsIgnoreCase("guard")) {
            applyArmorBoost(player, level);
            
            // Reduce durability on successful ability trigger
            Enchantment ench = Enchantment.getByKey(new NamespacedKey("octorpg", "defender"));
            if (ench != null) {
                OctoRPG.getInstance().getEnchantmentDurabilityManager().handleSingleUsage(player, item, ench);
            }
        }
    }

    private void applyArmorBoost(Player player, int level) {
        double amount = level * 2.0; // 2 armor points per level
        long durationTicks = 100L; // 5 seconds

        AttributeModifier modifier = new AttributeModifier(
                new org.bukkit.NamespacedKey(OctoRPG.getInstance(), "defender_boost"),
                amount,
                AttributeModifier.Operation.ADD_NUMBER
        );

        player.getAttribute(Attribute.ARMOR).addModifier(modifier);
        player.sendMessage(CC.translate("&7[&bRPG&7] &aDefender enchantment increased your armor by &f" + (int)amount + " &afor 5 seconds!"));

        // Remove after duration
        OctoRPG.getInstance().getServer().getScheduler().runTaskLater(OctoRPG.getInstance(), () -> {
            if (player.isOnline()) {
                player.getAttribute(Attribute.ARMOR).removeModifier(modifier);
            }
        }, durationTicks);
    }
}
