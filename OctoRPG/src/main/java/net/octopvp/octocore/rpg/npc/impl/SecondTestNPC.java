package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SecondTestNPC extends BaseNPC {
    @Override
    public String getName() {
        return "Testy2";
    }

    @Override
    public String getId() {
        return "TEST_NPC_2";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }

    @Override
    public String getSkin() {
        return "DigitalDerg";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add(String.format("Hey, %s!", player.getName()));
        gossip.add("It's nice to see you around here!");
        return gossip;
    }
}
