package net.octopvp.octocore.paper.hooks;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;

public class CitizensHook implements Hook{
    @Override
    public void onEnable() {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER,"abc");
    }
    @Override
    public void onDisable() {}
}
