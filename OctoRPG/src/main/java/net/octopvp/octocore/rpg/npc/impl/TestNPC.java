package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;

public class TestNPC extends BaseNPC {
    @Override
    public String getName() {
        return "Testy";
    }

    @Override
    public String getId() {
        return "TEST_NPC";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.CREEPER;
    }
}
