package net.octopvp.octocore.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.octopvp.octocore.velocity.manager.OnlinePlayersManager;
import net.octopvp.octocore.velocity.objects.OnlinePlayerData;

public class PlayerListener {
    @Subscribe
    public void onJoin(LoginEvent event) {
        OnlinePlayersManager.getDataMap().put(
                event.getPlayer().getUniqueId(),
                new OnlinePlayerData(
                        event.getPlayer().getUniqueId()
                )
        );
    }

    @Subscribe
    public void onLeave(DisconnectEvent event) {
        OnlinePlayersManager.getDataMap().remove(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onSwitch(ServerPostConnectEvent event) {
        RegisteredServer previousServer = event.getPreviousServer();
        if (previousServer != null) {
            event.getPlayer().sendMessage(
                    Component.text("Sending you to " + event.getPlayer().getCurrentServer().get().getServerInfo().getName() + "...")
                            .color(net.kyori.adventure.text.format.NamedTextColor.GRAY)
            );
            OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(event.getPlayer().getUniqueId());
            if (data == null) {
                OnlinePlayersManager.getDataMap().put(
                        event.getPlayer().getUniqueId(),
                        new OnlinePlayerData(
                                event.getPlayer().getUniqueId()
                        )
                );
            }
            data.getNodes().clear();
            data.getCachedPermResults().clear();
        }
    }
}
