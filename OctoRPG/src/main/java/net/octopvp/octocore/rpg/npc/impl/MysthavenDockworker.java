package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MysthavenDockworker extends BaseNPC {
    @Override
    public String getName() {
        return "Dockworker";
    }

    @Override
    public String getId() {
        return "MYSTHAVEN_DOCKWORKER";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }

    @Override
    public String getSkinTexture() {
        // https://minesk.in/d4b7b6a22851404eade35dec562fded8
        // http://textures.minecraft.net/texture/b66bc80f002b10371e2fa23de6f230dd5e2f3affc2e15786f65bc9be4c6eb71a
        return "ewogICJ0aW1lc3RhbXAiIDogMTY2NjMwMDI4NDI1NSwKICAicHJvZmlsZUlkIiA6ICJiNTM5NTkyMjMwY2I0MmE0OWY5YTRlYmYxNmRlOTYwYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJtYXJpYW5hZmFnIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I2NmJjODBmMDAyYjEwMzcxZTJmYTIzZGU2ZjIzMGRkNWUyZjNhZmZjMmUxNTc4NmY2NWJjOWJlNGM2ZWI3MWEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==";
    }

    @Override
    public String getSkinSignature() {
        return "duGmueLBvFNcwpQdRUgjB+GfLkCUFv/3Ripjv35ytisCmKL6R6GjOmlUmjrMvQ+e0HFEAzeiRN9p9tUYBflTnGC7+zCpKbgyuinl6zldDCM7Ofr2VA8IYUE1GVrrdhK5XSm8ngNAX7mSIHfylb8U2tll7ks+Tr6p1mvPRCwMVplS8LGauPVwNARSUkmH9+yF3DeegBXTYTBUeaVe9k5SQVag2vdA2AorWmrSv0jiazd2JukQDwuZyDiz8cQ3DXDeM7nthWBCBebud9wy95lQ14/2IywvZJbMDgj8JqvftSTvIbUX0oSkp+QOnJRHy6EAJQDGBl24gZc99RIgvlaqvF79Tg78Y8XqsPzXzoVz0kUBp45kq+8zx4BSVT4QB1cpdJqqOPdlR3Yn0PVoRideawPaaITwp0zLRNdVxM4ATL/UwPKavx9mRAErWmKTBvM9nBBTcxo+dw5B5sHmVR47VGsakDk91o9tSbOYdVRw0Na3iFhgjZ5svU7GJ4/OZOJuVgxNRdH/5+l3PsLyb29Lpei5SrBUOLGruplF+2Ch3QbGumfFGuejrrxSKriY0hv4jD3daIvADGZJmuJ/K7pTB9xDylT7iy7v9c99go/l1JU6DTmYle1PeqplUjcxmspGFwHQPzv7D4A5/2w38KFq13wllNeTgFGJGED4Ssuq3lY=";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add("Welcome to Aetheria, traveler!");
        gossip.add("Mysthaven is a beautiful city.");
        gossip.add("I love the smell of the ocean here.");
        gossip.add("Don't you?");
        return gossip;
    }
}
