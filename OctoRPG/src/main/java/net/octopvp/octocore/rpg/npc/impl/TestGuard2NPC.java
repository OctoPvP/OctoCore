package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TestGuard2NPC extends BaseNPC {
    @Override
    public String getName() {
        return "Guard Sound";
    }

    @Override
    public String getId() {
        return "TEST_GUARD_2";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }

    @Override
    public String getSkin() {
        return "hypersound70";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add("You salute the guard as you walk by.");
        return gossip;
    }
}
