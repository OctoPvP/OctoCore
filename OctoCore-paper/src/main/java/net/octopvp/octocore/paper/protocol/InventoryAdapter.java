package net.octopvp.octocore.paper.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.PlayerCloseInventoryEvent;
import net.octopvp.octocore.paper.api.events.PlayerOpenInventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InventoryAdapter extends PacketAdapter {
    private static Set<UUID> currentlyOpen;

    static {
        InventoryAdapter.currentlyOpen = new HashSet<>();
    }

    public InventoryAdapter() {
        super(OctoCore.getInstance(), PacketType.Play.Client.CLIENT_COMMAND, PacketType.Play.Client.CLOSE_WINDOW);
    }

    public static Set<UUID> getCurrentlyOpen() {
        return InventoryAdapter.currentlyOpen;
    }

    public void onPacketReceiving(final PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketContainer packet = event.getPacket();
        if (packet.getType() == PacketType.Play.Client.CLIENT_COMMAND && packet.getClientCommands().size() != 0 && packet.getClientCommands().read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            if (!InventoryAdapter.currentlyOpen.contains(player.getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(OctoCore.getInstance(), () -> Bukkit.getPluginManager().callEvent(new PlayerOpenInventoryEvent(player)));
            }
            InventoryAdapter.currentlyOpen.add(player.getUniqueId());
        } else if (packet.getType() == PacketType.Play.Client.CLOSE_WINDOW) {
            if (InventoryAdapter.currentlyOpen.contains(player.getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(OctoCore.getInstance(), () -> Bukkit.getPluginManager().callEvent(new PlayerCloseInventoryEvent(player)));
            }
            InventoryAdapter.currentlyOpen.remove(player.getUniqueId());
        }
    }
}
