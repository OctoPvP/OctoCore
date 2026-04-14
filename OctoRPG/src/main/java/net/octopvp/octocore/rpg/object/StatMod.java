package net.octopvp.octocore.rpg.object;

public @interface StatMod {
    int vitality() default 0;

    int resilience() default 0;

    int strength() default 0;

    int agility() default 0;

    int intelligence() default 0;

    int karma() default 0;

    int health() default 0;
}
