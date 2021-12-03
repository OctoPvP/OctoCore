package net.octopvp.octocore.paper.hologram;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class Holograms {
  public static HologramBuilder forPlayer(Player player) {
    return new HologramBuilder(Collections.singleton(player.getUniqueId()));
  }
  
  public static HologramBuilder forPlayers(Collection<Player> players) {
    if (players == null)
      return new HologramBuilder(null); 
    return new HologramBuilder((Collection<UUID>)players.stream().map(Entity::getUniqueId).collect(Collectors.toSet()));
  }
  
  public static HologramBuilder newHologram() {
    return forPlayers(null);
  }
}
