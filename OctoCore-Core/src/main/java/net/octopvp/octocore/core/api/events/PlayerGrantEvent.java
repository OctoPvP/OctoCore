package net.octopvp.octocore.core.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class PlayerGrantEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Grant grant;
    private final PlayerData targetData;
    private final CommandSender executor;

    private boolean cancelled;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

