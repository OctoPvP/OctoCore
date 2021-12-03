package net.octopvp.octocore.paper.hologram;

import java.util.LinkedHashSet;
import java.util.Set;

public final class HologramRegistry {
  private static final Set<Hologram> holograms = new LinkedHashSet<>();
  
  public static Set<Hologram> getHolograms() {
    return holograms;
  }
}
