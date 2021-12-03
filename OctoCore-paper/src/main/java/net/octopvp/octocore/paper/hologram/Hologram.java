package net.octopvp.octocore.paper.hologram;

import java.util.Collection;
import java.util.List;
import org.bukkit.Location;

public interface Hologram {
  void send();
  
  void destroy();
  
  void addLines(String... paramVarArgs);
  
  void setLine(int paramInt, String paramString);
  
  void setLines(Collection<String> paramCollection);
  
  List<String> getLines();
  
  Location getLocation();
}
