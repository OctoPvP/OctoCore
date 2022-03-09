package net.octopvp.octocore.paper.hologram;

import com.google.common.collect.ImmutableList;
import net.octopvp.octocore.paper.OctoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class UpdatingHologram extends BaseHologram {
    private long interval = 1L;

    private final Consumer<Hologram> updateFunction;

    private boolean showing = false;

    public UpdatingHologram(UpdatingHologramBuilder builder) {
        super(builder);
        this.interval = builder.getInterval();
        this.updateFunction = builder.getUpdateFunction();
    }

    public void send() {
        if (this.showing) {
            update();
            return;
        }
        super.send();
        this.showing = true;
        (new BukkitRunnable() {
            public void run() {
                if (!UpdatingHologram.this.showing) {
                    cancel();
                } else {
                    UpdatingHologram.this.update();
                }
            }
        }).runTaskTimerAsynchronously(OctoCore.getInstance(), 0L, this.interval * 20L);
    }

    public void setLine(int index, String line) {
        if (index > rawLines().size() - 1) {
            rawLines().add(new HologramLine(line));
        } else if (rawLines().get(index) != null) {
            rawLines().get(index).setText(line);
        } else {
            rawLines().set(index, new HologramLine(line));
        }
    }

    public void setLines(Collection<String> lines) {
        Collection<UUID> viewers = getViewers();
        if (viewers == null)
            viewers = ImmutableList.copyOf(Bukkit.getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                destroy0(player);
        }
        rawLines().clear();
        for (String line : lines)
            rawLines().add(new HologramLine(line));
    }

    public void destroy() {
        super.destroy();
        this.showing = false;
    }

    public void update() {
        this.updateFunction.accept(this);
        if (!this.showing)
            return;
        Collection<UUID> viewers = getViewers();
        if (viewers == null)
            viewers = ImmutableList.copyOf(Bukkit.getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                update(player);
        }
        this.lastLines = this.lines;
    }
}
