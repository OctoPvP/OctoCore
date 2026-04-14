package net.octopvp.octocore.rpg.item.impl;

import net.octopvp.octocore.rpg.item.BaseRPGItem;
import net.octopvp.octocore.rpg.object.ItemType;
import net.octopvp.octocore.rpg.object.Rarity;
import net.octopvp.octocore.rpg.object.StatModifier;
import net.octopvp.octocore.rpg.object.WeaponType;
import org.bukkit.Material;

public class Greatsword extends BaseRPGItem {

    @Override
    public String getId() {
        return "GREATSWORD";
    }

    @Override
    public String getName() {
        return "Greatsword";
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_SWORD;
    }

    @Override
    public String getDescription() {
        return "A massive sword that requires both hands to wield effectively.";
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
        mod.setStrength(15);
        mod.setAgility(-8);
        return mod;
    }
}
