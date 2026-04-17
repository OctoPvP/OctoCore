package net.octopvp.octocore.rpg.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatModifier {
    private int vitality = 0;
    private int resilience = 0;
    private int strength = 0;
    private int agility = 0;
    private int intelligence = 0;
    private int karma = 0;
    private int health = 0;

    private String info = "";

    public int getVitality() {
        return vitality;
    }

    public void setVitality(int vitality) {
        this.vitality = vitality;
    }

    public int getResilience() {
        return resilience;
    }

    public void setResilience(int resilience) {
        this.resilience = resilience;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public StatModifier() {
    }

    public StatModifier(StatMod mod) {
        this.vitality = mod.vitality();
        this.resilience = mod.resilience();
        this.strength = mod.strength();
        this.agility = mod.agility();
        this.intelligence = mod.intelligence();
        this.karma = mod.karma();
        this.health = mod.health();
    }
}
