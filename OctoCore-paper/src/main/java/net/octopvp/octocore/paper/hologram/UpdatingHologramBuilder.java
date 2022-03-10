package net.octopvp.octocore.paper.hologram;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class UpdatingHologramBuilder extends HologramBuilder {
    private long interval;

    private Consumer<Hologram> updateFunction;

    protected UpdatingHologramBuilder(HologramBuilder hologramBuilder) {
        super(hologramBuilder.getViewers());
        this.lines = hologramBuilder.getLines();
        at(hologramBuilder.getLocation());
    }

    protected long getInterval() {
        return this.interval;
    }

    protected Consumer<Hologram> getUpdateFunction() {
        return this.updateFunction;
    }

    public UpdatingHologramBuilder interval(long time, TimeUnit unit) {
        this.interval = unit.toSeconds(time);
        return this;
    }

    public UpdatingHologramBuilder onUpdate(Consumer<Hologram> onUpdate) {
        this.updateFunction = onUpdate;
        return this;
    }

    public Hologram build() {
        return new UpdatingHologram(this);
    }
}
