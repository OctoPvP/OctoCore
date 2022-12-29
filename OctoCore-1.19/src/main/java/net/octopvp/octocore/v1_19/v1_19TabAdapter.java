package net.octopvp.octocore.v1_19;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.octopvp.octocore.core.utils.tab.TabAdapter;
import net.octopvp.octocore.core.utils.tab.skin.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class v1_19TabAdapter extends TabAdapter {
    public static final v1_19TabAdapter INSTANCE = new v1_19TabAdapter();
    private final Map<Player, GameProfile[]> profiles = new HashMap<>();
    private final List<Player> initialized = new ArrayList<>();

    /**
     * Send a packet to the player
     *
     * @param player the player
     * @param packet the packet to send
     */
    private void sendPacket(Player player, Packet<?> packet) {
        this.getPlayerConnection(player).getConnection().send(packet);
    }

    /**
     * Send the header and footer to a player
     *
     * @param player the player to send the header and footer to
     * @param header the header to send
     * @param footer the footer to send
     * @return the current adapter instance
     */
    @Override
    public TabAdapter sendHeaderFooter(Player player, String header, String footer) {
        player.setPlayerListHeaderFooter(header, footer);
        return this;
    }

    /**
     * Update the skin on the tablist for a player
     *
     * @param skinData the data of the new skin
     * @param index    the index of the profile
     * @param player   the player to update the skin for
     */
    @Override
    public void updateSkin(String[] skinData, int index, Player player) {
        final GameProfile profile = this.profiles.get(player)[index];
        final Property property = profile.getProperties().get("textures").iterator().next();
        final ServerPlayer ServerPlayer = this.getServerPlayer(profile);

        skinData = skinData != null && skinData.length >= 1 && !skinData[0].isEmpty() && !skinData[1].isEmpty()
                ? skinData
                : SkinType.DARK_GRAY.getSkinData();

        if (!property.getSignature().equals(skinData[1]) || !property.getValue().equals(skinData[0])) {
            profile.getProperties().remove("textures", property);
            profile.getProperties().put("textures", new Property("textures", skinData[0], skinData[1]));

            this.sendInfoPacket(player, ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ServerPlayer);
        }
    }


    /**
     * Check if the player should be able to see the fourth row
     *
     * @param player the player
     * @return whether they should be able to see the fourth row
     */
    @Override
    public int getMaxElements(Player player) {
        return 80;
    }

    /**
     * Send an entry's data to a player
     *
     * @param player the player
     * @param axis   the axis of the entry
     * @param ping   the ping to display on the entry's position
     * @param text   the text to display on the entry's position
     * @return the current adapter instance
     */
    @Override
    public TabAdapter sendEntryData(Player player, int axis, int ping, String text) {
        final GameProfile profile = this.profiles.get(player)[axis];
        final ServerPlayer serverPlayer = this.getServerPlayer(profile);

        Component adventureText = Component.text(text);
        serverPlayer.listName = new AdventureComponent(adventureText);
        //ServerPlayer.ping = ping;
        // the fuck happened to spigot 1.17

        this.setupScoreboard(player, text, profile.getName());
        //this.sendInfoPacket(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.c, ServerPlayer);
        this.sendInfoPacket(player, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY, serverPlayer);
        return this;
    }

    /**
     * Add fake players to the player's tablist
     *
     * @param player the player to send the fake players to
     * @return the current adapter instance
     */
    @Override
    public TabAdapter addFakePlayers(Player player) {
        if (!initialized.contains(player)) {
            for (int i = 0; i < 80; i++) {
                final GameProfile profile = this.profiles.get(player)[i];
                final ServerPlayer ServerPlayer = this.getServerPlayer(profile);

                this.sendInfoPacket(player, ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ServerPlayer);
            }

            initialized.add(player);
        }

        return this;
    }

    /**
     * Get an entity player by a profile
     *
     * @param profile the profile
     * @return the entity player
     */
    private ServerPlayer getServerPlayer(GameProfile profile) {
        final MinecraftServer server = MinecraftServer.getServer();
        final ServerLevel worldServer = server.getAllLevels().iterator().next();

        return new ServerPlayer(server, worldServer, profile);
    }

    /**
     * Hide all real players from the tab
     *
     * @param player the player
     * @return the current adapter instance
     */
    @Override
    public TabAdapter hideRealPlayers(Player player) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            this.hidePlayer(player, target);
        }

        return this;
    }

    /**
     * Hide a real player om the tablist
     *
     * @param player the player to hide the player from
     * @param target the player to hide
     * @return the current adapter instance
     */
    @Override
    public TabAdapter hidePlayer(Player player, Player target) {
        if (player.canSee(target) || target.equals(player)) {
            // TODO: make sure this works, originally it was part of EnumPlayerInfoAction, but it seems to be moved into its own packet
            //this.sendInfoPacket(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.b, target);
            this.sendPacket(player, new ClientboundPlayerInfoRemovePacket(Arrays.asList(target.getUniqueId())));
        }

        return this;
    }

    /**
     * Show all real players on the tab
     *
     * @param player the player
     * @return the current adapter instance
     */
    @Override
    public TabAdapter showRealPlayers(Player player) {
        if (!this.initialized.contains(player)) {
            //final ChannelPipeline pipeline = this.getPlayerConnection(player).a.k.pipeline();
            final ChannelPipeline pipeline = this.getPlayerConnection(player).getConnection().channel.pipeline();

            while (pipeline.get("packet_handler") == null) {
                this.showRealPlayers(player);
            }

            pipeline.addBefore(
                    "packet_handler",
                    player.getName(),
                    this.createShowListener(player)
            );
        }

        return this;
    }

    /**
     * Show a real player to a player
     *
     * @param player the player
     * @param target the player to show to the other player
     * @return the current adapter instance
     */
    @Override
    public TabAdapter showPlayer(Player player, Player target) {
        this.sendInfoPacket(player, ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, target);
        return this;
    }

    /**
     * Create the listener required to show the players
     *
     * @param player the player to create it for
     * @return the handler
     */
    private ChannelDuplexHandler createShowListener(Player player) {
        return new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {

                if (packet instanceof final ClientboundAddEntityPacket entitySpawn) {
                    final Player target = Bukkit.getPlayer(entitySpawn.getUUID());

                    if (target != null) {
                        showPlayer(player, target);
                    }
                } else if (packet instanceof ClientboundRespawnPacket) {
                    showPlayer(player, player);
                }

                super.write(context, packet, promise);
            }
        };
    }

    /**
     * Get the {@link ServerGamePacketListener} of a player
     *
     * @param player the player to get the player connection object from
     * @return the object
     */
    private ServerGamePacketListener getPlayerConnection(Player player) {
        return ((CraftPlayer) player).getHandle().connection;
    }

    /**
     * Send the {@link ClientboundPlayerInfoUpdatePacket} to a player
     *
     * @param player the player
     * @param action the action
     * @param target the target
     */
    private void sendInfoPacket(Player player, ClientboundPlayerInfoUpdatePacket.Action action, ServerPlayer target) {
        this.sendPacket(player, new ClientboundPlayerInfoUpdatePacket(action, target));
    }

    /**
     * Send the {@link ClientboundPlayerInfoUpdatePacket} to a player
     *
     * @param player the player
     * @param action the action
     * @param target the target
     */
    private void sendInfoPacket(Player player, ClientboundPlayerInfoUpdatePacket.Action action, Player target) {
        this.sendInfoPacket(player, action, ((CraftPlayer) target).getHandle());
    }

    /**
     * Create a new game profile
     *
     * @param index  the index of the profile
     * @param text   the text to display
     * @param player the player to make the profiles for
     */
    @Override
    public void createProfiles(int index, String text, Player player) {
        if (!this.profiles.containsKey(player)) {
            this.profiles.put(player, new GameProfile[80]);
        }

        if (this.profiles.get(player).length < index + 1 || this.profiles.get(player)[index] == null) {
            final GameProfile profile = new GameProfile(UUID.randomUUID(), text);
            final String[] skinData = SkinType.DARK_GRAY.getSkinData();

            profile.getProperties().put("textures", new Property("textures", skinData[0], skinData[1]));

            this.profiles.get(player)[index] = profile;
        }
    }
}
