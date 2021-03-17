package net.octopvp.octocore.paper.manager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.SubChannels;
import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Arrays;

public class PluginMsgManager implements Manager, PluginMessageListener {
    String[] pluginMessagesOut = new String[]{PluginMsgChannels.SPIGOT_TO_BUNGEE, PluginMsgChannels.LUNAR_CLIENT};
    String[] pluginMessagesIn = new String[] {PluginMsgChannels.BUNGEE_TO_SPIGOT};
    @Override
    public void init(OctoCorePaper plugin) {
        Arrays.asList(pluginMessagesOut).forEach(m -> Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(OctoCorePaper.getInstance(),m));
        Arrays.asList(pluginMessagesIn).forEach(m-> Bukkit.getServer().getMessenger().registerIncomingPluginChannel(OctoCorePaper.getInstance(), m,this));
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
