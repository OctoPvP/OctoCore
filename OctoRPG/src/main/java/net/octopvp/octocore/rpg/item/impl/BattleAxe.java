package net.octopvp.octocore.rpg.item.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.item.BaseRPGItem;
import net.octopvp.octocore.rpg.object.ItemType;
import net.octopvp.octocore.rpg.object.Rarity;
import net.octopvp.octocore.rpg.object.StatModifier;
import net.octopvp.octocore.rpg.object.WeaponType;
import org.bukkit.Material;

public class BattleAxe extends BaseRPGItem {

    @Override
    public String getId() {
        return "BATTLE_AXE";
    }

    @Override
    public String getName() {
        return "Battle Axe";
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_AXE;
    }

    @Override
    public String getDescription() {
        return "A heavy hitting cleaver. Very slow attack speed, massive damage.";
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
        return WeaponType.AXE;
    }

    @Override
    public StatModifier getStatModifierWhenHolding() {
        StatModifier mod = new StatModifier();
        mod.setStrength(10);
        mod.setAgility(-5);
        return mod;
    }
}
