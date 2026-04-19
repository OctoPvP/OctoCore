package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TestWelcomerNPC extends BaseNPC {
    @Override
    public String getName() {
        return "Two";
    }

    @Override
    public String getId() {
        return "TEST_WELCOMER";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }

    @Override
    public String getSkin() {
        return "22reps";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add(String.format("Hey, welcome to Aetheria, %s!", player.getName()));
        return gossip;
    }
}
