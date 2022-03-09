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

public class PluginMsgManager extends Manager implements PluginMessageListener {
    private static final String[] in = new String[]{/*PluginMsgChannels.PLUGIN_MSG,PluginMsgChannels.LUNAR_CLIENT,PluginMsgChannels.BUNGEE*/};
    private static final String[] out = new String[]{/*PluginMsgChannels.PLUGIN_MSG,PluginMsgChannels.LUNAR_CLIENT,PluginMsgChannels.BUNGEE*/};

    @Override
    public void init(OctoCore plugin) {
        Arrays.asList(out).forEach(m -> Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(OctoCore.getInstance(), m));
        Arrays.asList(in).forEach(m -> Bukkit.getServer().getMessenger().registerIncomingPluginChannel(OctoCore.getInstance(), m, this));
    }

    @Override
    public void disable() {

    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!Arrays.asList(in).contains(channel))
            return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();
        switch (subChannel) {
            case PluginMsgChannels.SubChannels.SYNC:
                break;
        }
    }
}
