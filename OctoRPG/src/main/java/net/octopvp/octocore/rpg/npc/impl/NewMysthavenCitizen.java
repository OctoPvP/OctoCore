package net.octopvp.octocore.rpg.npc.impl;

import net.kyori.adventure.text.Component;
import net.octopvp.octocore.rpg.npc.NPCType;
import net.octopvp.octocore.rpg.npc.DisplayData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class NewMysthavenCitizen implements NPCType {

    @Override
    public String getName() {
        return "Mysthaven Citizen";
    }

    @Override
    public Optional<String> getTitle() {
        return Optional.of("Citizen");
    }

    @Override
    public String getTypeId() {
        return "MYSTHAVEN_CITIZEN";
    }

    @Override
    public List<Component> getGossip(Player player) {
        List<Component> gossip = new ArrayList<>();
        gossip.add(Component.text("Welcome to Mysthaven, traveler!"));
        gossip.add(Component.text("Hello!"));
        gossip.add(Component.text("How are you?"));
        gossip.add(Component.text("The Torchflower is a great place to stay."));
        gossip.add(Component.text("I can't wait to move out of my tent and into a real home."));
        gossip.add(Component.text("Move to the new town, they said. It's done being built, they said."));
        gossip.add(Component.text("I'm so excited to finally be here!"));
        Random rand = new Random();

        //return a random item from the list
        return List.of(gossip.get(rand.nextInt(gossip.size())));
    }

    @Override
    public DisplayData getDisplayData() {
        return new DisplayData(EntityType.PLAYER,
                "ewogICJ0aW1lc3RhbXAiIDogMTY0MjI4MDk0ODE0NiwKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkyYWI0OWNiN2I3YmFhMTkzNThjM2FlNzA1MWU0MWQ5MjY1NDgyYTg0YTc5ZDM1YzU5YWNlZDk1Zjg0YWFjZGEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "oFsh1h7kEf8xefyBWh1UEx7OHASg/BQYnV4xtMvl2kd0RYRC3oRsxQZxfPFcM8URLSkTQIHiYPFQL7H8gN1x69i1etLgUrBXlNEcgSMANVDb4EItccY/1EBXcnJRhYTASxUYjMEYrxU8j0wxNBanM8Z0x7qr+kEV/hZ0Pmf02fL1/NpfFtABIx2bagRpaqU/uywtYhgFJvpx0NyU/62I/Bmhzfkv1yDFQVJkGkzkuTI8VzjMPd3UdbBhIsMVYz1Rdcl6Mi4AQp16OR0VYdg08xpHxt5x4W+USbV7uWii61ObK6TN/nLhJ7ldUh8tp1chvRZ8kCn3NRm1LbL1D/XqSufL41eqtHPaUbu3Wszn/FYGFD4IyuopOmcYqlyzCzwKBaRdjdU01823arrgQW/D4RS0Ad76NXXm45D4+1DiKLuYuikuMlNbOoLIwRJ9uQyQvBEvG3UGYDD9SG4OZqfmOqcuEMnLm/ApUQa1QsNti7DHptEcK4AWvTlfMBGKPci7T6KutTy2Tap6T4QglflRpL4fzk2p14ISXBCdnIvuWz8laP+myG0zfB8NsWkQ1oBZAkKIZjt4MlanEHE9kAXXK0fGqBc9bL58Ul2l40QbFbrkSuS0hACT+2qpbDGldn4Ej/PG8cTFf/aZKlu2SCDBNB//YgDdkhmrTgQ+mDsdrfs="
        );
    }

    @Override
    public void onInteract(Player player) {
        // :)
    }
}
