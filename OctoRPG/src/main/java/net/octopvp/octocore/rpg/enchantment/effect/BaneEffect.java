package net.octopvp.octocore.rpg.enchantment.effect;

import net.octopvp.octocore.rpg.enchantment.EnchantmentEffect;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class BaneEffect implements EnchantmentEffect {
    @Override
    public String getEnchantmentId() {
        return "bane";
    }

    @Override
    public void onHitEntity(Player player, Entity victim, EntityDamageByEntityEvent event, ItemStack item, int level) {
        if (!(victim instanceof org.bukkit.entity.LivingEntity livingVictim)) return;
        if (livingVictim.hasMetadata("NPC")) return;

        long durationMs = 5000;
        long expiry = System.currentTimeMillis() + durationMs;

        if (livingVictim instanceof Player victimPlayer) {
            RPGPlayerData victimData = RPGPlayerManager.getInstance().getData(victimPlayer.getUniqueId());
            if (victimData != null) {
                victimData.applyBlight(5);
            }
        } else {
            livingVictim.setMetadata("rpg_blight", new org.bukkit.metadata.FixedMetadataValue(net.octopvp.octocore.rpg.OctoRPG.getInstance(), expiry));
        }

        // Debug logging for the attacker
        RPGPlayerData attackerData = RPGPlayerManager.getInstance().getData(player.getUniqueId());
        if (attackerData != null && attackerData.isDebug()) {
            String victimName = (livingVictim instanceof Player p) ? p.getName() : livingVictim.getType().name();
            player.sendMessage(net.octopvp.octocore.common.util.CC.translate("&7[&bRPG Debug&7] &fApplied &dBLIGHT &fto &d" + victimName + " &7(Bane Enchant)"));
        }
    }
}
