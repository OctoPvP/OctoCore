package net.octopvp.octocore.paper.hologram;

import org.bukkit.Location;

import java.util.*;

public class HologramBuilder {
    protected List<String> lines = new ArrayList<>();
    private final Collection<UUID> viewers;
    private Location location;

    protected HologramBuilder(Collection<UUID> viewers) {
        this.viewers = viewers;
    }

    protected Collection<UUID> getViewers() {
        return this.viewers;
    }

    protected Location getLocation() {
        return this.location;
    }

    protected List<String> getLines() {
        return this.lines;
    }

    public HologramBuilder addLines(Iterable<String> lines) {
        for (String line : lines)
            this.lines.add(line);
        return this;
    }

    public HologramBuilder addLines(String... lines) {
        this.lines.addAll(Arrays.asList(lines));
        return this;
    }

    public HologramBuilder at(Location location) {
        this.location = location;
        return this;
    }

    public UpdatingHologramBuilder updates() {
        return new UpdatingHologramBuilder(this);
    }

    public Hologram build() {
        return new BaseHologram(this);
    }
}
