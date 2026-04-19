package net.octopvp.octocore.rpg.npc;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.rpg.util.runnable.Tasks;
import net.octopvp.octocore.rpg.util.ClassUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NPCManager {
    @Getter
    @Setter
    private NPCProvider provider;
    private final List<String> talkingPlayers = new ArrayList<>();
    private final Map<String, GameNPC> npcs = new HashMap<>();
    private final Map<String, NPCType> npcTypes = new HashMap<>();

    /**
     * Registers an NPC, spawning it in the world. Automatically registers the NPC's type if it is not already registered.
     * @param npc The NPC to register
     * @throws IllegalArgumentException if an NPC with the same ID is already registered
     */
    public void registerNPC(GameNPC npc) {
        if (npcs.containsKey(npc.getId())) {
            throw new IllegalArgumentException("NPC with ID " + npc.getId() + " is already registered");
        }
        npcTypes.put(npc.getNpcType().getTypeId(), npc.getNpcType());
        if (provider != null) {
            provider.spawn(npc);
        }
        npcs.put(npc.getId(), npc);
    }

    /**
     * Registers an NPC, spawning it in the world.
     * @param typeId The type of the NPC
     * @param id The ID of the NPC
     * @param location The location to spawn the NPC
     * @throws NoSuchElementException if no NPC type with the given ID is registered
     */
    public void registerNPC(String typeId, String id, Location location) {
        NPCType type = getNPCTypeById(typeId);
        if (type == null) {
            Logger.error("Tried to spawn NPC with type " + typeId + " but no such type is registered");
            throw new NoSuchElementException("No such NPC type registered: " + typeId);
        }
        registerNPC(new GameNPC(id, location, type));
    }

    /**
     * Deregisters an NPC, despawning it from the world
     * @param npc The NPC to deregister
     */
    public void deregisterNPC(GameNPC npc) {
        if (provider != null) {
            provider.despawn(npc);
        }
        npcs.remove(npc.getId());
    }

    /**
     * Registers all NPCTypes in a package, so they can be found by getNPCTypeById.
     * @param packageName The package to register NPCs in
     */
    public void registerTypesInPackage(String packageName) {
        Logger.debug("Registering NPC types in package " + packageName);
        ClassUtil.getClassesInPackage(packageName).forEach(clazz -> {
            if (NPCType.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                try {
                    NPCType type = (NPCType) clazz.getDeclaredConstructor().newInstance();
                    Logger.debug("Registering " + type.getName());
                    npcTypes.put(type.getTypeId(), type);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Gets an NPC type by its ID
     * @param type The ID of the NPC type
     */
    public void registerType(NPCType type) {
        npcTypes.put(type.getTypeId(), type);
    }

    /**
     * Checks if a player is currently talking to an NPC
     * @param player The player to check
     * @return true if the player is talking to an NPC
     */
    public boolean isTalking(Player player) {
        return isTalking(player.getUniqueId().toString());
    }

    /**
     * Checks if a player is currently talking to an NPC
     * @param uuid The UUID of the player to check
     * @return true if the player is talking to an NPC
     */
    public boolean isTalking(String uuid) {
        return talkingPlayers.contains(uuid);
    }

    /**
     * Sets whether a player is talking to an NPC
     * @param player The player to set
     * @param talking Whether the player is talking to an NPC
     */
    public void setTalking(Player player, boolean talking) {
        setTalking(player.getUniqueId().toString(), talking);
    }

    /**
     * Sets whether a player is talking to an NPC
     * @param uuid The UUID of the player to set
     * @param talking Whether the player is talking to an NPC
     */
    public void setTalking(String uuid, boolean talking) {
        if (talking) {
            if (!talkingPlayers.contains(uuid)) {
                talkingPlayers.add(uuid);
            }
        } else {
            talkingPlayers.remove(uuid);
        }
    }

    /**
     * Called when a player interacts with an NPC. Runs gossip and onInteract for the NPC.
     * @param player The player interacting with the NPC
     * @param npc The NPC being interacted with
     */
    public void interactionCallback(Player player, GameNPC npc) {
        if (isTalking(player)) {
            return;
        }
        setTalking(player, true);
        NPCType type = npc.getNpcType();
        Component nameplate = Component.text("[").color(NamedTextColor.GRAY)
                .append(Component.text(type.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text("] ").color(NamedTextColor.GRAY));
        int delayTicks = 0;
        List<Component> gossip = type.getGossip(player);
        if (gossip != null) {
            for (Component line : gossip) {
                Component message = nameplate.append(line.colorIfAbsent(NamedTextColor.YELLOW));
                Tasks.runLater(() -> player.sendMessage(message), delayTicks);
                delayTicks += 20;
            }
        }

        Tasks.runLater(() -> type.onInteract(player), delayTicks);
        Tasks.runLater(() -> setTalking(player, false), delayTicks);
    }

    /**
     * Gets an NPC by its ID, if it is currently registered
     * @param id The ID of the NPC
     * @return The NPC with the given ID, or null if no such NPC exists
     */
    @Nullable
    public GameNPC getNPC(String id) {
        return npcs.get(id);
    }

    public NPCType getNPCTypeById(String id) {
        return npcTypes.get(id);
    }
}
