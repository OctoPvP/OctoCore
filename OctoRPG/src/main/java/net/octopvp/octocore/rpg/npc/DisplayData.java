package net.octopvp.octocore.rpg.npc;

import lombok.Getter;
import net.octopvp.octocore.common.util.Logger;
import org.bukkit.entity.EntityType;

import java.util.Optional;
/**
 * Represents the mob or player/skin to display for an NPC (or maybe other entities in the future).
 */
@Getter
public class DisplayData {
    private final EntityType entityType;
    private Optional<String> playerSkinUsername = Optional.empty();
    private Optional<String> texture = Optional.empty();
    private Optional<String> signature = Optional.empty();

    /**
     * Create a new DisplayData object for a non-player entity.
     * @param entityType The entity type to display.
     */
    public DisplayData(EntityType entityType) {
        if (entityType == EntityType.PLAYER) {
            throw new IllegalArgumentException("Must specify a skin for player entities!");
        }
        this.entityType = entityType;
    }

    /**
     * Create a new DisplayData object for a player entity. <a href="https://mineskin.org/">Mineskin</a> is recommended.
     * @param entityType The entity type to display.
     * @param texture The texture of the player's skin.
     * @param signature The signature of the player's skin.
     */
    public DisplayData(EntityType entityType, String texture, String signature) {
        this.entityType = entityType;
        this.texture = Optional.of(texture);
        this.signature = Optional.of(signature);
        if (entityType != EntityType.PLAYER) {
            Logger.warn("Specified a skin for a non-player entity!");
        }
    }

    /**
     * Create a new DisplayData object for a player entity by using the skin of a player with the specified username.
     * Not recommended for use in production as players can change their skins, but useful for testing.
     * @param entityType The entity type to display.
     * @param playerSkinUsername The username of the player whose skin to use.
     */
    @Deprecated
    public DisplayData(EntityType entityType, String playerSkinUsername) {
        this.entityType = entityType;
        this.playerSkinUsername = Optional.of(playerSkinUsername);
        if (entityType != EntityType.PLAYER) {
            Logger.warn("Specified a skin for a non-player entity!");
        }
    }
}
