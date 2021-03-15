package net.octopvp.octocore.paper.manager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.SubChannels;
import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMsgManager implements Manager, PluginMessageListener {
    @Override
    public void init(OctoCorePaper plugin) {
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(OctoCorePaper.getInstance(), PluginMsgChannels.BUNGEE_TO_SPIGOT,this);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(OctoCorePaper.getInstance(),PluginMsgChannels.SPIGOT_TO_BUNGEE);
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
