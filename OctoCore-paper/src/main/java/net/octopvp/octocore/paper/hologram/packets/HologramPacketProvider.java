package net.octopvp.octocore.paper.hologram.packets;

import net.octopvp.octocore.paper.hologram.HologramLine;
import org.bukkit.Location;

public interface HologramPacketProvider {
  HologramPacket getPacketsFor(Location paramLocation, HologramLine paramHologramLine);
}
