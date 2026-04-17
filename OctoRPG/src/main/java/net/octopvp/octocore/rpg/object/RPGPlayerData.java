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

    public void setStunned(int stunned) {
        this.stunned = stunned;
    }

    public int getStunned() {
        return stunned;
    }

    private int attributePoints = 0;
    private int abilityPoints = 0;
    private int skillPoints = 0;
    private int classPoints = 0;
    private int boagsEaten = 0;
    private long xp = 0;
    
    private String baseClassName;
    private java.util.HashMap<String, com.google.gson.JsonObject> classData = new java.util.HashMap<>();
    private java.util.List<Long> completedQuests = new java.util.ArrayList<>();
    private java.util.Map<Long, Long> inProgressQuests = new java.util.HashMap<>();

    public void addXp(int xp) {
        this.xp += xp;
        int needed = StatCalculator.xpNeededForNextLevel(level + 1);
        if (this.xp >= needed) {
            level++;
            attributePoints += 3;
            abilityPoints++;
            classPoints++;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(CC.translate("&6&lYou Leveled Up!"));
                player.sendMessage(CC.translate("&7You are now level &b&l" + level + "&r&7!"));
                player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            }
        }
    }

    public boolean isQuestCompleted(long questId) {
        return completedQuests.contains(questId);
    }

    public void setQuestCompleted(long questId) {
        if (!completedQuests.contains(questId)) {
            completedQuests.add(questId);
        }
        inProgressQuests.remove(questId);
    }

    public void startQuest(long questId) {
        inProgressQuests.put(questId, System.currentTimeMillis());
    }

    private transient int vitalityAfterCalc = 0;
    private transient int resilianceAfterCalc = 0;
    private transient int strengthAfterCalc = 0;
    private transient int agilityAfterCalc = 0;
    private transient int intelligenceAfterCalc = 0;
    private transient int karmaAfterCalc = 0;
    private transient int extraHealthAfterCalc = 0;
    private transient int baseManaAfterCalc = 0;
    private transient long blightUntil = 0;

    public void applyBlight(int seconds) {
        this.blightUntil = System.currentTimeMillis() + (seconds * 1000L);
    }

    public boolean hasBlight() {
        return System.currentTimeMillis() < blightUntil;
    }

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

        tickCount++;

        // Recalculate stats every 0.1s for instant weapon switching
        recalculateStats(player);

        // Periodically refresh lore of held item (every 0.5s)
        if (tickCount % 5 == 0) {
            ItemStack inHand = player.getInventory().getItemInMainHand();
            if (inHand != null && inHand.getType() != org.bukkit.Material.AIR) {
                OctoRPG.getInstance().getItemManager().rebuildLore(inHand);
            }
        }

        // Mana Regeneration - Every 1 second (10 update ticks at 2L each)
        if (tickCount % 10 == 0) {
            int baseMana = StatCalculator.calculateMana(level, resilianceAfterCalc);
            int regen = StatCalculator.calculateManaToRegen(level, intelligenceAfterCalc, baseMana);
            this.currentManaLeft = Math.min(currentManaLeft + regen, baseMana);
            this.baseManaAfterCalc = baseMana;
        }

        // Stun logic
        if (stunned > 0) {
            stunned--; 
            if (stunned == 0) {
                applyEngineStats(player);
            }
        }

        // Always apply speed/health to ensure immediate responsiveness
        applyEngineStats(player);
    }

    /**
     * Aggregates base stats and item modifiers into 'AfterCalc' variables.
     * These variables are transient and are NEVER saved to MongoDB.
     * This prevents 'stat loops' where scaled values could be accidentally persisted.
     */
    private void recalculateStats(Player player) {
        int vitality = baseVitality;
        int resilience = baseResilience;
        int strength = baseStrength;
        int agility = baseAgility;
        int intelligence = baseIntelligence;
        int karma = baseKarma;
        int extraHealth = 0;

        List<StatModifier> finalModifiers = new ArrayList<>();
        List<BaseRPGItem> doneInventoryItems = new ArrayList<>();
        
        // 1. Scan entire inventory for "In Inventory" bonuses (unique per item type)
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            CustomItem customItem = OctoRPG.getInstance().getItemManager().getCustomItem(item);
            if (customItem instanceof BaseRPGItem baseItem) {
                StatModifier ininv = baseItem.getStatModifierWhenInInventory();
                if (ininv != null && !doneInventoryItems.contains(baseItem)) {
                    finalModifiers.add(ininv);
                    doneInventoryItems.add(baseItem);
                }
            }
        }

        // 2. Scan equipped items for "Holding/Wearing" bonuses
        // Main Hand
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        CustomItem mainCustom = OctoRPG.getInstance().getItemManager().getCustomItem(mainHand);
        if (mainCustom instanceof BaseRPGItem baseItem) {
            StatModifier mod = baseItem.getStatModifierWhenHolding();
            if (mod != null) finalModifiers.add(mod);
        }

        // Off Hand
        ItemStack offHand = player.getInventory().getItemInOffHand();
        CustomItem offCustom = OctoRPG.getInstance().getItemManager().getCustomItem(offHand);
        if (offCustom instanceof BaseRPGItem baseItem) {
            StatModifier mod = baseItem.getStatModifierWhenHolding();
            if (mod != null) finalModifiers.add(mod);
        }

        // Armor
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null) continue;
            CustomItem armorCustom = OctoRPG.getInstance().getItemManager().getCustomItem(armor);
            if (armorCustom instanceof BaseRPGItem baseItem) {
                StatModifier mod = baseItem.getStatModifierWhenHolding(); // "Holding" acts as "Wearing" for armor
                if (mod != null) finalModifiers.add(mod);
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
        this.baseManaAfterCalc = StatCalculator.calculateMana(level, resilianceAfterCalc);
    }

    private void applyEngineStats(Player player) {
        double multiplier = StatCalculator.getLevelScaling(level);

        // Apply Max Health
        double targetMaxHealth = StatCalculator.calculateHealth(level, vitalityAfterCalc) + extraHealthAfterCalc;
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr != null) {
            if (Math.abs(maxHealthAttr.getBaseValue() - targetMaxHealth) > 0.01) {
                maxHealthAttr.setBaseValue(targetMaxHealth);
                if (debug && Math.abs(targetMaxHealth - lastAppliedMaxHealth) > 0.01) {
                    player.sendMessage(CC.translate("&7[&bRPG Debug&7] &fStats Updated -> HP: &a" + String.format("%.1f", targetMaxHealth) + " &7(LVL Mult: " + String.format("%.2f", multiplier) + "x)"));
                }
                lastAppliedMaxHealth = targetMaxHealth;
            }
        }

        // Apply Walk Speed
        float targetSpeed = stunned > 0 ? 0 : StatCalculator.calculateSpeed(level, agilityAfterCalc);
        if (Math.abs(player.getWalkSpeed() - targetSpeed) > 0.001f) {
            player.setWalkSpeed(targetSpeed);
            if (debug && Math.abs(targetSpeed - lastAppliedSpeed) > 0.001f) {
                player.sendMessage(CC.translate("&7[&bRPG Debug&7] &fStats Updated -> Speed: &a" + String.format("%.3f", targetSpeed) + " &7(LVL Mult: " + String.format("%.2f", multiplier) + "x)"));
            }
            lastAppliedSpeed = targetSpeed;
        }
    }

    public void updateActionBar(Player player) {
        if (player == null) return;
        
        int totalHealth = (int) Math.ceil(player.getHealth() + player.getAbsorptionAmount());
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        int maxHealth = maxHealthAttr != null ? (int) Math.floor(maxHealthAttr.getValue()) : 20;
        
        boolean absorption = player.getAbsorptionAmount() > 0;
        
        NamedTextColor healthColor = NamedTextColor.RED;
        if (hasBlight()) {
            healthColor = NamedTextColor.LIGHT_PURPLE;
        } else if (absorption) {
            healthColor = NamedTextColor.GOLD;
        }
        
        Component actionBar = Component.text(totalHealth + "/" + maxHealth + "❤")
                .color(healthColor)
                .append(Component.text(" • ").color(NamedTextColor.GRAY))
                .append(Component.text(currentManaLeft + "/" + baseManaAfterCalc + "❂").color(NamedTextColor.AQUA));
                
        player.sendActionBar(actionBar);
    }
}
