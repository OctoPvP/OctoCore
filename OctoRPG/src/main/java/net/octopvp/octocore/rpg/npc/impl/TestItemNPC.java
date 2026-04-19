package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.menu.MainItemsMenu;
import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TestItemNPC extends BaseNPC {
    @Override
    public String getName() {
        return "Salesman Noah";
    }

    @Override
    public String getId() {
        return "TEST_ITEM_GIVER";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }

    @Override
    public String getSkin() {
        return "StrawHat_KoITta";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add("I don't have anything to sell yet! Want something for free?");
        return gossip;
    }

    @Override
    public void afterGossip(Player player) {
        new MainItemsMenu().open(player);
    }
}
