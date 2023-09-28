package net.octopvp.octocore.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.velocity.OctoCoreVelocity;
import net.octopvp.octocore.velocity.objects.VelocityConfiguration;

import java.util.Random;

@RequiredArgsConstructor
public class PingListener {
    private final OctoCoreVelocity plugin;
    private final Random random = new Random();

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        VelocityConfiguration config = plugin.getConfig();
        VelocityConfiguration.PingConfig pingConfig = config.getMotds().get(config.getDefaultMotd());
        if (config.isRandomMotd()) {
            pingConfig = config.getMotds().get(
                    config.getMotds().keySet().toArray(new String[0])[random.nextInt(config.getMotds().size())]
            );
        }
        if (pingConfig == null)
            return;
        event.setPing(pingConfig.generatePing(event.getPing()));
    }
}
