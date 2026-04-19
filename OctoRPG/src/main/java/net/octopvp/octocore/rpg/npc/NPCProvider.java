package net.octopvp.octocore.rpg.npc;

import org.bukkit.event.Listener;

/**
 * Provides methods to spawn and despawn npcs. Importantly, providers are expected
 * to listen to NPC interactions and handle them accordingly.
 */
public interface NPCProvider extends Listener {
    /**
     * Spawns the npc, if it doesn't already exist in the world
     * @param npc the npc to spawn
     */
    void spawn(GameNPC npc);
    /**
     * Despawns the npc, if it exists
     * @param npc the npc to despawn
     */
    void despawn(GameNPC npc);
}
