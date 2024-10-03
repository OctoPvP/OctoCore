package net.octopvp.octocore.velocity.listeners;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.octopvp.octocore.common.interfaces.manager.IDatabaseManager;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.velocity.OctoCoreVelocity;
import net.octopvp.octocore.velocity.VelocityServerImpl;
import net.octopvp.octocore.velocity.manager.OnlinePlayersManager;
import net.octopvp.octocore.velocity.objects.OctoCorePermissionsProvider;
import net.octopvp.octocore.velocity.objects.OnlinePlayerData;
import org.bson.Document;

@AllArgsConstructor
public class PlayerListener {
    private OctoCoreVelocity plugin;
    private final VelocityServerImpl velocityServerImpl;

    @Subscribe
    public void onJoin(LoginEvent event) {
        Logger.debug("Player " + event.getPlayer().getUsername() + " joined");
        OnlinePlayersManager.getDataMap().put(
                event.getPlayer().getUniqueId(),
                new OnlinePlayerData(
                        event.getPlayer().getUniqueId()));
    }

    @Subscribe
    public void onLeave(DisconnectEvent event) {
        Logger.debug("Player " + event.getPlayer().getUsername() + " left");
        OnlinePlayersManager.getDataMap().remove(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onSwitch(ServerPostConnectEvent event) {
        RegisteredServer previousServer = event.getPreviousServer();
        if (previousServer != null) {
            event.getPlayer().sendMessage(
                    Component
                            .text("Sending you to "
                                    + event.getPlayer().getCurrentServer().get().getServerInfo().getName() + "...")
                            .color(net.kyori.adventure.text.format.NamedTextColor.GRAY));
            OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(event.getPlayer().getUniqueId());
            if (data == null) {
                OnlinePlayersManager.getDataMap().put(
                        event.getPlayer().getUniqueId(),
                        new OnlinePlayerData(
                                event.getPlayer().getUniqueId()));
            }
            data.getNodes().clear();
            data.getCachedPermResults().clear();
        }
    }

    // see
    // https://github.com/LuckPerms/LuckPerms/blob/master/velocity/src/main/java/me/lucko/luckperms/velocity/listeners/VelocityConnectionListener.java#L61
    @Subscribe
    public void onPermissionSetup(PermissionsSetupEvent e, Continuation continuation) {
        if (!(e.getSubject() instanceof Player)) {
            continuation.resume();
            return;
        }
        final Player player = (Player) e.getSubject();
        Logger.debug("Setting up permissions for " + player.getUsername());
        plugin.getProxyServer().getScheduler()
                .buildTask(plugin, () -> {
                    // TODO load player data if needed. Need to implement mongodb first
                    //
                    Logger.debug(" - Loading player data");
                    try {
                        // Initialize your MongoClient here
                        // get the username and password / host and port from the config
                        // ...
                        // use the getMongoClient() from VelocityServerImpl.java
                        // MongoClient mongoClient = plugin.getMongoClient();

                        // MongoClient mongoClient = MongoClients.create("");
                        IDatabaseManager databaseManager = velocityServerImpl.getDatabaseManager();

                        // Get the database and collection
                        MongoDatabase database = databaseManager.getDatabase();
                        MongoCollection<Document> collection = database.getCollection("pdata");

                        // Query for the player's UUID
                        Document playerData = collection.find(Filters.eq("uuid", player.getUniqueId().toString()))
                                .first();

                        if (playerData != null) {
                            // Player data found, process it as needed
                            Logger.debug("Player data loaded: " + playerData.toJson());
                        } else {
                            // No player data found for the given UUID
                            Logger.debug("No player data found for UUID: " + player.getUniqueId());
                        }
                    } catch (Exception ex) {
                        Logger.error("Error loading player data", ex);
                    }

                    Logger.debug(" - Setting provider");
                    e.setProvider(new OctoCorePermissionsProvider(player));
                    continuation.resume();
                }).schedule();
    }
}
