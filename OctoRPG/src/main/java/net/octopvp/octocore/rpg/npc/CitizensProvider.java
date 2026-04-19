package net.octopvp.octocore.rpg.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class CitizensProvider implements NPCProvider {
    public NPCRegistry registry = CitizensAPI.createInMemoryNPCRegistry("OctoRPG");
    Map<String, Integer> npcIds = new HashMap<>();

    public void spawn(GameNPC npc) {
        NPCType type = npc.getNpcType();
        DisplayData display = type.getDisplayData();
        NPC citizensNpc = registry.createNPC(display.getEntityType(), "", npc.getLocation());
        citizensNpc.data().set("rpgid", npc.getId());
        citizensNpc.getOrAddTrait(HologramTrait.class).clear();
        
        if (type instanceof BaseNPC && ((BaseNPC) type).shouldWatchPlayer()) {
            citizensNpc.getOrAddTrait(LookClose.class).lookClose(true);
            citizensNpc.getOrAddTrait(LookClose.class).setRange(10);
        }
        
        int line = 0;
        if (type.getTitle().isPresent()) {
            citizensNpc.getOrAddTrait(HologramTrait.class).setLine(line, String.format("%s<%s>", CC.GREEN, type.getTitle().get()));
            line++;
        }
        citizensNpc.getOrAddTrait(HologramTrait.class).setLine(line, CC.GREEN + type.getName());
        citizensNpc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        citizensNpc.setProtected(true);

        if (display.getEntityType() == EntityType.PLAYER && display.getPlayerSkinUsername().isPresent()) {
            citizensNpc.getOrAddTrait(SkinTrait.class).setSkinName(display.getPlayerSkinUsername().get());
        }
        if (display.getEntityType() == EntityType.PLAYER && display.getTexture().isPresent() && display.getSignature().isPresent()) {
            citizensNpc.getOrAddTrait(SkinTrait.class).setSkinPersistent("Dummy", display.getSignature().get(), display.getTexture().get());
        }
        npcIds.put(npc.getId(), citizensNpc.getId());
    }

    public void despawn(GameNPC npc) {
        if (npcIds.containsKey(npc.getId())) {
            NPC citizensNpc = registry.getById(npcIds.get(npc.getId()));
            if (citizensNpc != null) {
                registry.deregister(citizensNpc);
            }
            npcIds.remove(npc.getId());
        }
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        onNPCClick(event);
    }

    @EventHandler
    public void onNPCLeftClick(NPCLeftClickEvent event) {
        onNPCClick(event);
    }

    private void onNPCClick(NPCClickEvent event) {
        String id = event.getNPC().data().get("rpgid");
        Player player = event.getClicker().getPlayer();
        if (player == null)
            return;
        if (id == null)
            return;

        NPCManager manager = OctoRPG.getInstance().getNpcManager();
        GameNPC npc = manager.getNPC(id);

        if (npc != null) {
            manager.interactionCallback(player, npc);
        }
    }
}
