package net.octopvp.octocore.paper.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class PlayerGrantEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    private final Grant grant;
    private final PlayerData targetData;
    private final CommandSender executor;

    private boolean cancelled;

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

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

