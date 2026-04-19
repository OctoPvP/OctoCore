package net.octopvp.octocore.core.setup;

import net.octopvp.octocore.common.redis.packets.GlobalBroadcastPacket;
import net.octopvp.octocore.common.redis.packets.GlobalCommandPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.listeners.ChatListener;
import net.octopvp.octocore.core.listeners.DeathListener;
import net.octopvp.octocore.core.listeners.JoinLeaveListener;
import net.octopvp.octocore.core.listeners.PunishmentListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class SetupListeners implements Setup {
    private static final Listener[] listeners = new Listener[]{new PunishmentListener(), new JoinLeaveListener(), new ChatListener(), new DeathListener()};

    @Override
    public void setup(OctoCore plugin) {
        PluginManager plm = Bukkit.getPluginManager();
        for (Listener listener : listeners) plm.registerEvents(listener, plugin);

        GlobalBroadcastPacket.setImplementation(message -> {
            Bukkit.broadcastMessage(CC.translate(message));
        });

        GlobalCommandPacket.setImplementation(command -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }

    @Override
    public void disable(OctoCore plugin) {
    }
}
