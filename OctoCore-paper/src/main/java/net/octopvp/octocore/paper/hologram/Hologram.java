package net.octopvp.octocore.paper.hologram;

import org.bukkit.Location;

import java.util.Collection;
import java.util.List;

public interface Hologram {
    void send();

    void destroy();

    void addLines(String... paramVarArgs);

    void setLine(int paramInt, String paramString);

    List<String> getLines();

    void setLines(Collection<String> paramCollection);

    Location getLocation();
}
