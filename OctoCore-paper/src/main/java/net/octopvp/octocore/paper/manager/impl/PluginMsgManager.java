package net.octopvp.octocore.paper.manager.impl;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Arrays;

public class PluginMsgManager implements Manager, PluginMessageListener {
    String[] pluginMessagesOut = new String[]{PluginMsgChannels.SPIGOT_TO_BUNGEE, PluginMsgChannels.LUNAR_CLIENT};
    String[] pluginMessagesIn = new String[] {PluginMsgChannels.BUNGEE_TO_SPIGOT};
    @Override
    public void init(OctoCore plugin) {
        Arrays.asList(pluginMessagesOut).forEach(m -> Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(OctoCore.getInstance(),m));
        Arrays.asList(pluginMessagesIn).forEach(m-> Bukkit.getServer().getMessenger().registerIncomingPluginChannel(OctoCore.getInstance(), m,this));
    }

    @Override
    public void disable(OctoCore plugin) {

    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equalsIgnoreCase(PluginMsgChannels.BUNGEE_TO_SPIGOT))
            return;
        ByteArrayDataInput in = ByteStreams.newDataInput( bytes );
        String subChannel = in.readUTF();
        switch (subChannel){

        }
    }
}
