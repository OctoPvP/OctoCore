package net.octopvp.octocore.rpg.object;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.BaseRPGItem;
import net.octopvp.octocore.rpg.item.CustomItem;
import net.octopvp.octocore.rpg.util.StatCalculator;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RPGPlayerData {
    private final UUID uuid;

    private int level = 1;
    private int baseVitality = 0;
    private int baseResilience = 0;
    private int baseStrength = 0;
    private int baseAgility = 0;
    private int baseIntelligence = 0;
    private int baseKarma = 0;
    
    private int currentManaLeft = 0;
    
    private int stunned = 0;

    private transient int vitalityAfterCalc = 0;
    private transient int resilianceAfterCalc = 0;
    private transient int strengthAfterCalc = 0;
    private transient int agilityAfterCalc = 0;
    private transient int intelligenceAfterCalc = 0;
    private transient int karmaAfterCalc = 0;
    private transient int extraHealthAfterCalc = 0;
    private transient int baseManaAfterCalc = 0;

    public int getStrengthAfterCalc() {
        return strengthAfterCalc;
    }

    public RPGPlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public void join(Player player) {
        this.currentManaLeft = StatCalculator.calculateMana(level, baseResilience);
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(StatCalculator.calculateHealth(level, baseVitality));
        }
        player.setHealthScale(20d);
        player.setHealthScaled(true);
    }

    private transient int tickCount = 0;
    private transient float lastAppliedSpeed = -1;
    private transient double lastAppliedMaxHealth = -1;
    private boolean debug = false;

    public void update() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        // Recalculate stats every 0.1s for instant weapon switching
        recalculateStats(player);

        // Periodically refresh lore of held item (every 0.5s or on change)
        if (tickCount % 5 == 0) {
            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (inHand != null && inHand.getType() != org.bukkit.Material.AIR) {
                OctoRPG.getInstance().getItemManager().rebuildLore(inHand);
            }
        }

        // Light updates every 0.1s (2 ticks)
        if (stunned > 0) {
            stunned--; 
            if (stunned == 0) {
                applyEngineStats(player);
            }
        }

        // Always apply speed/health to ensure immediate responsiveness to stun/unstun
        applyEngineStats(player);
    }

    private void recalculateStats(Player player) {
        int vitality = baseVitality;
        int resilience = baseResilience;
        int strength = baseStrength;
        int agility = baseAgility;
        int intelligence = baseIntelligence;
        int karma = baseKarma;
        int extraHealth = 0;

        List<StatModifier> finalModifiers = new ArrayList<>();
        List<BaseRPGItem> doneItems = new ArrayList<>();
        
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item == null) continue;
            CustomItem customItem = OctoRPG.getInstance().getItemManager().getCustomItem(item);
            if (customItem instanceof BaseRPGItem baseItem) {
                StatModifier ininv = baseItem.getStatModifierWhenInInventory();
                if (ininv != null && !doneItems.contains(baseItem)) {
                    finalModifiers.add(ininv);
                    doneItems.add(baseItem);
                }
                
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                StatModifier holding = baseItem.getStatModifierWhenHolding();
                if (holding != null && mainHand != null && mainHand.equals(item)) {
                    finalModifiers.add(holding);
                }
            }
        }

        for (StatModifier modifier : finalModifiers) {
            vitality += modifier.getVitality();
            resilience += modifier.getResilience();
            strength += modifier.getStrength();
            agility += modifier.getAgility();
            intelligence += modifier.getIntelligence();
            karma += modifier.getKarma();
            extraHealth += modifier.getHealth();
        }

        this.vitalityAfterCalc = vitality;
        this.resilianceAfterCalc = resilience;
        this.strengthAfterCalc = strength;
        this.agilityAfterCalc = agility;
        this.intelligenceAfterCalc = intelligence;
        this.karmaAfterCalc = karma;
        this.extraHealthAfterCalc = extraHealth;

        int baseMana = StatCalculator.calculateMana(level, resilience);
        int regen = StatCalculator.calculateManaToRegen(level, baseMana);
        this.currentManaLeft = Math.min(currentManaLeft + regen, baseMana);
        this.baseManaAfterCalc = baseMana;
    }

    private void applyEngineStats(Player player) {
        // Apply Max Health
        double targetMaxHealth = StatCalculator.calculateHealth(level, vitalityAfterCalc) + extraHealthAfterCalc;
        if (targetMaxHealth != lastAppliedMaxHealth) {
            AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(targetMaxHealth);
                if (debug) player.sendMessage(CC.translate("&7[&bRPG Debug&7] &fApplied Max Health: &a" + targetMaxHealth));
                lastAppliedMaxHealth = targetMaxHealth;
            }
        }

        // Apply Walk Speed
        float targetSpeed = stunned > 0 ? 0 : StatCalculator.calculateSpeed(level, agilityAfterCalc);
        if (targetSpeed != lastAppliedSpeed) {
            player.setWalkSpeed(targetSpeed);
            if (debug) player.sendMessage(CC.translate("&7[&bRPG Debug&7] &fApplied Walk Speed: &a" + targetSpeed + " &7(Agility: " + agilityAfterCalc + ")"));
            lastAppliedSpeed = targetSpeed;
        }
    }

    public void updateActionBar(Player player) {
        if (player == null) return;
        
        int totalHealth = (int) Math.ceil(player.getHealth() + player.getAbsorptionAmount());
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        int maxHealth = maxHealthAttr != null ? (int) Math.floor(maxHealthAttr.getValue()) : 20;
        
        boolean absorption = player.getAbsorptionAmount() > 0;
        
        Component actionBar = Component.text(totalHealth + "/" + maxHealth + "❤")
                .color(absorption ? NamedTextColor.GOLD : NamedTextColor.RED)
                .append(Component.text(" • ").color(NamedTextColor.GRAY))
                .append(Component.text(currentManaLeft + "/" + baseManaAfterCalc + "❂").color(NamedTextColor.AQUA));
                
        player.sendActionBar(actionBar);
    }
}
