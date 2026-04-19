package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TestGuardNPC extends BaseNPC {
    @Override
    public String getName() {
        return "Guard Percival";
    }

    @Override
    public String getId() {
        return "TEST_GUARD";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }

    @Override
    public String getSkin() {
        return "Percival_0";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add("The guard silently watches you.");
        gossip.add("It's probably best you leave them alone.");
        return gossip;
    }
}
