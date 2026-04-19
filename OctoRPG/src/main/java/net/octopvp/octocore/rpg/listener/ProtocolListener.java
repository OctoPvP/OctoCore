package net.octopvp.octocore.rpg.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.octopvp.octocore.rpg.OctoRPG;
import org.bukkit.Particle;

public class ProtocolListener {
    public static void register(OctoRPG plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_PARTICLES) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES) {
                    Particle particle = event.getPacket().getNewParticles().read(0).getParticle();
                    if (particle == Particle.DAMAGE_INDICATOR || particle == Particle.CRIT || particle == Particle.ENCHANTED_HIT) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }
}
