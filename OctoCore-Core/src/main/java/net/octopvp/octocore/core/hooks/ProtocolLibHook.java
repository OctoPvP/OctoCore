package net.octopvp.octocore.core.hooks;

import com.comphenix.protocol.ProtocolLibrary;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.protocol.InventoryAdapter;
import net.octopvp.octocore.core.protocol.PingAdapter;
import net.octopvp.octocore.core.utils.runnable.runnables.LagCheck;
import org.bukkit.Bukkit;

public class ProtocolLibHook implements Hook {

    @Override
    public void onEnable() {
        //ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryAdapter());
            final PingAdapter ping = new PingAdapter();
            ProtocolLibrary.getProtocolManager().addPacketListener(ping);
            Bukkit.getPluginManager().registerEvents(ping, OctoCore.getInstance());
            new LagCheck().runTaskTimerAsynchronously(OctoCore.getInstance(), 100L, 100L);
            //ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new TabAdapter());
        }
    }

    @Override
    public void onDisable() {

    }
}
