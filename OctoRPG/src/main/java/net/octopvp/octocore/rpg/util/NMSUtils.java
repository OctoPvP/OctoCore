package net.octopvp.octocore.rpg.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.UUID;

/**
 * Utility class for entity-related operations that previously required NMS.
 * For Paper 1.21, most of these are now available via the Bukkit API.
 */
public class NMSUtils {

    public static Entity getEntityFromUUID(UUID uuid) {
        return Bukkit.getEntity(uuid);
    }

    public static double getEntityHeight(Entity entity) {
        return entity.getHeight();
    }

    public static double getEntityWidth(Entity entity) {
        return entity.getWidth();
    }

    public static BoundingBox getBoundingBox(Entity entity) {
        return entity.getBoundingBox();
    }

    // This was previously used to get UUID from an entity ID
    public static UUID getEntityUUIDFromID(World world, int id) {
        for (Entity entity : world.getEntities()) {
            if (entity.getEntityId() == id) {
                return entity.getUniqueId();
            }
        }
        return null;
    }
}
