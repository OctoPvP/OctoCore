package net.octopvp.octocore.rpg.item.impl;

import net.octopvp.octocore.rpg.item.BaseRPGItem;
import net.octopvp.octocore.rpg.object.ItemType;
import net.octopvp.octocore.rpg.object.Rarity;
import net.octopvp.octocore.rpg.object.StatModifier;
import net.octopvp.octocore.rpg.object.WeaponType;
import org.bukkit.Material;

public class Dagger extends BaseRPGItem {

    @Override
    public String getId() {
        return "DAGGER";
    }

    @Override
    public String getName() {
        return "Dagger";
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_SWORD;
    }

    @Override
    public String getDescription() {
        return "A fast hitting blade. Low damage, very high speed.";
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
        return WeaponType.DAGGER;
    }

    @Override
    public StatModifier getStatModifierWhenHolding() {
        StatModifier mod = new StatModifier();
        mod.setAgility(10);
        mod.setStrength(-2);
        return mod;
    }
}
