package net.octopvp.octocore.waterfall.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;

public class PingEvent implements Listener {
    @EventHandler(priority = 64)
    public void onPing(ProxyPingEvent event) {
        ProxyServer.getInstance().getLogger().info("Proxy Ping evt.");
        ServerPing response = event.getResponse();
        if (OctoCoreWaterfall.getInstance().getConfig().getBoolean("protocol.enabled"))
            response.getVersion().setName(OctoCoreWaterfall.getInstance().getConfig().getString("protocol.version"));
        StringBuilder motd = new StringBuilder();
        int i = 0;
        for (String s1 : OctoCoreWaterfall.getInstance().getConfig().getStringList("motd")) {
            i++;
            String s = ChatColor.translateAlternateColorCodes('&', StringUtils.centerText(s1));
            if (i == 1)
                motd.append("    ").append(s); //FIXME fix MOTD centering
            else motd.append("\n" + s);
        }
        response.setDescriptionComponent(new TextComponent(motd.toString()));
    }
}
