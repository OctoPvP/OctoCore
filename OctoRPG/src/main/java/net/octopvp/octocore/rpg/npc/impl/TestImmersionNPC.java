package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TestImmersionNPC extends BaseNPC {
    @Override
    public String getName() {
        return "CoolGamer";
    }

    @Override
    public String getId() {
        return "TEST_COOLGAMER";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }

    @Override
    public String getSkin() {
        return "C0olgAmer999";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add("CoolGamer is too cool to talk to you.");
        return gossip;
    }
}
