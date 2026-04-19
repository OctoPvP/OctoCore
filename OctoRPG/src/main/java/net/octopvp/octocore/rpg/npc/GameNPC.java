package net.octopvp.octocore.rpg.npc;

import lombok.Getter;
import org.bukkit.Location;

/**
 * Represents an instance of an NPC in the game world.
 */
@Getter
public class GameNPC {
    private final String id;
    private final Location location;
    private final NPCType npcType;

    public GameNPC(String id, Location location, NPCType npcType) {
        this.id = id;
        this.location = location;
        this.npcType = npcType;
    }
}
