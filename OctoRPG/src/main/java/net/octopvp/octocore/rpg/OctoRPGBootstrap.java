package net.octopvp.octocore.rpg;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class OctoRPGBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            Map<String, String> enchants = Map.ofEntries(
                // Major / Lost
                Map.entry("fire_aspect", "Fire Aspect"),
                Map.entry("seraph", "Seraph"),
                Map.entry("vampire", "Vampire"),
                Map.entry("flametongue", "Flametongue"),
                Map.entry("frostbrand", "Frostbrand"),
                Map.entry("shock", "Shock"),
                Map.entry("moonlight", "Moonlight"),
                
                // Minor
                Map.entry("accuracy", "Accuracy"),
                Map.entry("balance", "Balance"),
                Map.entry("comfort", "Comfort"),
                Map.entry("lightness", "Lightness"),
                Map.entry("brilliance", "Brilliance"),
                Map.entry("catscratch", "Catscratch"),
                Map.entry("vorpal", "Vorpal"),
                Map.entry("dragon_slayer", "Dragon Slayer"),
                Map.entry("wither", "Wither"),
                Map.entry("inventory_expansion", "Inventory Expansion"),
                
                // Special
                Map.entry("plus", "Plus"),
                Map.entry("prosperity", "Prosperity"),
                Map.entry("healing_water", "Healing Water"),
                Map.entry("critical_resistance", "Critical Resistance"),
                Map.entry("auto_shield", "Auto-Shield"),
                Map.entry("frostbolt", "Frostbolt"),
                Map.entry("lifesteal", "Lifesteal"),

                // Added from EnchantmentUtil / RPGCore
                Map.entry("chains", "Chains"),
                Map.entry("glide", "Glide"),
                Map.entry("experienced", "Experienced"),
                Map.entry("multipick", "Multipick"),
                Map.entry("wings", "Wings"),
                Map.entry("titanic_chains", "Titanic Chains"),
                Map.entry("bleed", "Bleed"),
                Map.entry("homing", "Homing"),
                Map.entry("luminosity", "Luminosity"),
                Map.entry("surefooted", "Surefooted"),
                Map.entry("volume", "Volume"),
                Map.entry("bane", "Bane")
            );

            for (Map.Entry<String, String> entry : enchants.entrySet()) {
                String id = entry.getKey();
                String name = entry.getValue();
                
                int maxLvl = 10;
                if (id.equals("bane")) maxLvl = 1;

                TagKey<ItemType> targetTag = ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON;
                if (id.equals("frostbolt") || id.equals("homing") || id.equals("accuracy")) {
                    targetTag = ItemTypeTagKeys.ENCHANTABLE_BOW;
                } else if (id.equals("multipick")) {
                    targetTag = ItemTypeTagKeys.ENCHANTABLE_MINING;
                } else if (id.equals("glide") || id.equals("wings")) {
                    targetTag = ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR;
                } else if (id.equals("surefooted")) {
                    targetTag = ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR;
                }

                final TagKey<ItemType> finalTargetTag = targetTag;
                final int finalMaxLvl = maxLvl;
                event.registry().register(
                        TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("octorpg", id)),
                        builder -> builder.description(Component.text(name))
                                .supportedItems(event.getOrCreateTag(finalTargetTag))
                                .anvilCost(1)
                                .maxLevel(finalMaxLvl)
                                .weight(10)
                                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                                .activeSlots(EquipmentSlotGroup.ANY)
                );
            }
        }));
    }
}
