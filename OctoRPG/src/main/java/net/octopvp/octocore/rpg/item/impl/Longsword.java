package net.octopvp.octocore.rpg.item.impl;

import net.octopvp.octocore.rpg.item.BaseRPGItem;
import net.octopvp.octocore.rpg.object.ItemType;
import net.octopvp.octocore.rpg.object.Rarity;
import net.octopvp.octocore.rpg.object.StatModifier;
import net.octopvp.octocore.rpg.object.WeaponType;
import org.bukkit.Material;

public class Longsword extends BaseRPGItem {

    @Override
    public String getId() {
        return "LONGSWORD";
    }

    @Override
    public String getName() {
        return "Longsword";
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_SWORD;
    }

    @Override
    public String getDescription() {
        return "A versatile sword with good reach and damage.";
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.WEAPON;
    }

    @Override
    public WeaponType getWeaponType() {
        return WeaponType.SWORD;
    }

    @Override
    public StatModifier getStatModifierWhenHolding() {
        StatModifier mod = new StatModifier();
        mod.setStrength(5);
        mod.setAgility(-2);
        return mod;
    }
}
