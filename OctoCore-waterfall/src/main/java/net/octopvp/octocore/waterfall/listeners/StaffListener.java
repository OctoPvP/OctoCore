package net.octopvp.octocore.waterfall.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;
import net.octopvp.octocore.waterfall.manager.StaffManager;

import java.util.concurrent.TimeUnit;

public class StaffListener implements Listener {
    @EventHandler(priority = 64)
    public void onSwitch(ServerSwitchEvent event){
        if (event.getPlayer().hasPermission(Permission.SEND_SWITCH_MESSAGE.getNode())){
            //StaffManager.sendSwitch(event.getFrom(),event.getPlayer());
            ProxyServer.getInstance().getScheduler().schedule(OctoCoreWaterfall.getInstance(),()->{
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),"bungeeinternal sendswitch " + event.getPlayer().getName() + " " + event.getFrom().getName());
            },0, TimeUnit.MILLISECONDS);
        }
    }
    @EventHandler(priority = 64)
    public void onJoin(PostLoginEvent event){
        if (event.getPlayer().hasPermission(Permission.SEND_JOIN_MESSAGE.getNode())){
            ProxyServer.getInstance().getScheduler().schedule(OctoCoreWaterfall.getInstance(),()-> {
                        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "bungeeinternal sendjoin " + event.getPlayer().getName());
                        },0,TimeUnit.MILLISECONDS);
            //StaffManager.join(event.getPlayer().getServer().getInfo(),event.getPlayer());
        }
    }
    @EventHandler(priority = 64)
    public void onLeave(PlayerDisconnectEvent event){
        if (event.getPlayer().hasPermission(Permission.SEND_LEAVE_MESSAGE.getNode())){
            //StaffManager.leave(event.getPlayer().getServer().getInfo(),event.getPlayer());
            ProxyServer.getInstance().getScheduler().schedule(OctoCoreWaterfall.getInstance(),()-> {
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),"bungeeinternal sendleave " + event.getPlayer().getName());
                },0,TimeUnit.MILLISECONDS);
        }
    }
}
