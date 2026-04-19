package net.octopvp.octocore.rpg.npc;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public interface NPCType {

    /**
     * This name is displayed to the player in-game.
     * Uncolored parts of the component will be re-colored to the default color, so
     * you should not include color codes in the component unless absolutely necessary.
     * @return Name of the NPC. Ex. "Guard"
     */
    String getName();

    /**
     * @return Optional title of the NPC. Ex. "Guard". Will be rendered under the NPC's name inside angle brackets.
     */
    Optional<String> getTitle();

    /**
     * @return Name of the NPC's type. Ex. MYSTHAVEN_GUARD
     */
    String getTypeId();

    /**
     * Gets NPC gossip. Uncolored parts of the component will
     * be recolored to the default color.
     * @param player The player interacting with the NPC
     * @return List of gossip components
     */
    List<Component> getGossip(Player player);
    /**
     * @return The display data for the NPC
     */
    DisplayData getDisplayData();

    /**
     * Called when a player interacts with the NPC. Runs AFTER all gossip from {@link NPCType#getGossip(Player)} is sent.
     * @param player
     */
    void onInteract(Player player);

}
