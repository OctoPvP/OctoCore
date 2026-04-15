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
