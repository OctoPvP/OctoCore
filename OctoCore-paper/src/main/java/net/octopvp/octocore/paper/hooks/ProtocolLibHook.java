package net.octopvp.octocore.paper.hooks;

import com.comphenix.protocol.ProtocolLibrary;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.hologram.HologramListener;
import net.octopvp.octocore.paper.protocol.InventoryAdapter;
import net.octopvp.octocore.paper.protocol.PingAdapter;
import net.octopvp.octocore.paper.utils.runnable.runnables.LagCheck;
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
            Bukkit.getPluginManager().registerEvents(new HologramListener(), OctoCore.getInstance());
        }
    }

    @Override
    public void onDisable() {

    }
}
