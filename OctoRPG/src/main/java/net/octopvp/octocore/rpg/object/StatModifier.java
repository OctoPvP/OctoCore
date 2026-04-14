package net.octopvp.octocore.rpg.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatModifier {
    private int
            vitality = 0,
            resilience = 0,
            strength = 0,
            agility = 0,
            intelligence = 0,
            karma = 0,
            health = 0;

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
