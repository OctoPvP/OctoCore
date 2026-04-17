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
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.manager.IDatabaseManager;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.velocity.OctoCoreVelocity;
import net.octopvp.octocore.velocity.VelocityServerImpl;
import net.octopvp.octocore.velocity.manager.OnlinePlayersManager;
import net.octopvp.octocore.velocity.objects.OctoCorePermissionsProvider;
import net.octopvp.octocore.velocity.objects.OnlinePlayerData;
import net.octopvp.octocore.velocity.redis.packet.impl.staff.StaffConnectPacket;
import net.octopvp.octocore.velocity.redis.packet.impl.staff.StaffLeavePacket;
import net.octopvp.octocore.velocity.redis.packet.impl.staff.StaffSwitchPacket;
import org.bson.Document;

import java.util.UUID;

import com.mongodb.client.model.Updates;
import com.velocitypowered.api.event.player.PlayerClientBrandEvent;
import com.velocitypowered.api.event.player.PlayerModInfoEvent;
import com.velocitypowered.api.event.player.PlayerSettingsChangedEvent;
import com.google.gson.JsonObject;

@AllArgsConstructor
public class PlayerListener {
    private OctoCoreVelocity plugin;
    private final VelocityServerImpl velocityServerImpl;

    @Subscribe
    public void onPlayerSettingsChanged(PlayerSettingsChangedEvent event) {
        Player player = event.getPlayer();
        com.velocitypowered.api.proxy.player.PlayerSettings settings = player.getPlayerSettings();
        JsonObject settingsJson = new JsonObject();
        settingsJson.addProperty("locale", settings.getLocale() != null ? settings.getLocale().toString() : "unknown");
        settingsJson.addProperty("viewDistance", settings.getViewDistance());
        settingsJson.addProperty("chatMode", settings.getChatMode().name());
        settingsJson.addProperty("mainHand", settings.getMainHand().name());
        settingsJson.addProperty("hasChatColors", settings.hasChatColors());

        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
            try {
                IDatabaseManager databaseManager = velocityServerImpl.getDatabaseManager();
                if (databaseManager != null && databaseManager.getDatabase() != null) {
                    MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("vdata");
                    collection.updateOne(Filters.eq("uuid", player.getUniqueId().toString()), Updates.set("playerSettings", settingsJson.toString()));
                }
            } catch (Exception ex) {
                Logger.error("Failed to save playerSettings for " + player.getUsername(), ex);
            }
        }).schedule();
    }

    @Subscribe
    public void onPlayerModInfo(PlayerModInfoEvent event) {
        Player player = event.getPlayer();
        if (!event.getModInfo().getMods().isEmpty()) {
            StringBuilder modsList = new StringBuilder();
            event.getModInfo().getMods().forEach(mod -> {
                modsList.append(mod.getId()).append(" (v").append(mod.getVersion()).append("), ");
            });
            String modsString = modsList.length() > 0 ? modsList.substring(0, modsList.length() - 2) : "None";
            Logger.info("[Client Mods] " + player.getUsername() + " (" + player.getRemoteAddress() + ") connected with mods: " + modsString);

            plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
                try {
                    IDatabaseManager databaseManager = velocityServerImpl.getDatabaseManager();
                    if (databaseManager != null && databaseManager.getDatabase() != null) {
                        MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("vdata");
                        collection.updateOne(Filters.eq("uuid", player.getUniqueId().toString()), Updates.set("clientMods", modsString));
                    }
                } catch (Exception ex) {
                    Logger.error("Failed to save clientMods for " + player.getUsername(), ex);
                }
            }).schedule();
        }
    }

    @Subscribe
    public void onPlayerClientBrand(PlayerClientBrandEvent event) {
        Player player = event.getPlayer();
        String brand = event.getBrand();
        Logger.info("[OctoCore-velocity] " + player.getUsername() + " (" + player.getRemoteAddress() + ") has connected using client brand: " + brand);

        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
            try {
                IDatabaseManager databaseManager = velocityServerImpl.getDatabaseManager();
                if (databaseManager != null && databaseManager.getDatabase() != null) {
                    MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("vdata");
                    collection.updateOne(Filters.eq("uuid", player.getUniqueId().toString()), Updates.set("clientBrand", brand));
                }
            } catch (Exception ex) {
                Logger.error("Failed to save clientBrand for " + player.getUsername(), ex);
            }
        }).schedule();
    }

    @Subscribe
    public void onJoin(LoginEvent event) {
        Logger.debug("Player " + event.getPlayer().getUsername() + " joined");
        OnlinePlayersManager.getDataMap().put(
                event.getPlayer().getUniqueId(),
                new OnlinePlayerData(
                        event.getPlayer().getUniqueId()));

        Player player = event.getPlayer();
        int protocolVersion = player.getProtocolVersion().getProtocol();
        String virtualHost = player.getVirtualHost().isPresent() ? player.getVirtualHost().get().getHostString() : "unknown";
        long ping = player.getPing();

        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
            try {
                IDatabaseManager databaseManager = velocityServerImpl.getDatabaseManager();
                if (databaseManager != null && databaseManager.getDatabase() != null) {
                    MongoCollection<Document> collection = databaseManager.getDatabase().getCollection("vdata");
                    collection.updateOne(Filters.eq("uuid", player.getUniqueId().toString()), 
                        Updates.combine(
                            Updates.set("protocolVersion", protocolVersion),
                            Updates.set("virtualHost", virtualHost),
                            Updates.set("ping", ping)
                        )
                    );
                }
            } catch (Exception ex) {
                Logger.error("Failed to save connection metadata for " + player.getUsername(), ex);
            }
        }).schedule();
    }

    @Subscribe
    public void onLeave(DisconnectEvent event) {
        Logger.debug("Player " + event.getPlayer().getUsername() + " left");
        OnlinePlayerData data = OnlinePlayersManager.getDataMap().remove(event.getPlayer().getUniqueId());
        if (data != null && data.hasPermission(Permissions.SEND_JOIN_MESSAGE).orElse(false)) {
            new StaffLeavePacket(event.getPlayer().getUsername(), OctoCoreCommon.getInstance().getServerName(), 0, data.isVanished()).send();
        }
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
                data = OnlinePlayersManager.getDataMap().get(event.getPlayer().getUniqueId());
            }
            if (data.hasPermission(Permissions.SEND_SWITCH_MESSAGE).orElse(false)) {
                new StaffSwitchPacket(event.getPlayer().getUsername(), previousServer.getServerInfo().getName(), event.getPlayer().getCurrentServer().get().getServerInfo().getName()).send();
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
                    Logger.debug(" - Loading player data");
                    try {
                        // 1. Get the Manager
                        IDatabaseManager databaseManager = velocityServerImpl.getDatabaseManager();

                        // SAFETY CHECK: Is the manager null? (Did the DB fail to connect on startup?)
                        if (databaseManager == null) {
                            Logger.error("DatabaseManager is null! Database might not be connected.");
                            continuation.resume(); // Let the player in, or disconnect them if DB is required
                            return;
                        }

                        MongoDatabase database = databaseManager.getDatabase();
                        // is db null
                        if (database == null) {
                            Logger.error("MongoDatabase is null! Check your config/connection.");
                            continuation.resume();
                            return;
                        }
                        MongoCollection<Document> collection = database.getCollection("vdata");

                        // Query for the player's UUID
                        Document playerData = collection.find(Filters.eq("uuid", player
                                .getUniqueId()
                                .toString())).first();
                        if (playerData != null) {
                            // Player data found, process it as needed
                            Logger.debug("Player data loaded: " + playerData.toJson());
                            OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(player.getUniqueId());
                            if (data != null) {
                                data.update();
                                if (data.hasPermission(Permissions.SEND_JOIN_MESSAGE).orElse(false)) {
                                    new StaffConnectPacket(player.getUsername(), OctoCoreCommon.getInstance().getServerName(), 0, data.isVanished()).send();
                                }
                            }
                        } else {
                            // No player data found for the given UUID
                            Logger.debug("No player data found for UUID: " + player.getUniqueId());
                        }
                    } catch (Exception ex) {
                        Logger.error("Error loading player data", ex.getMessage());
                        ex.printStackTrace();
                    }

                    Logger.debug(" - Setting provider");
                    e.setProvider(new OctoCorePermissionsProvider(player));
                    continuation.resume();
                }).schedule();
    }
}
