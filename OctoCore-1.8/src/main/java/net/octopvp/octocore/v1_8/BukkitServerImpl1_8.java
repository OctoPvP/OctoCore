package net.octopvp.octocore.v1_8;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.module.impl.scoreboard.DefaultStringScoreboardHandler;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BukkitServerImpl1_8 implements BukkitServerImplementation {
    private BukkitAudiences adventure;
    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(OctoCore.getInstance());
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    @Override
    public ScoreboardHandler<?> getScoreboardHandler() {
        return new DefaultStringScoreboardHandler();
    }

    @Override
    public void setTabHeaderFooter(Player player, Component header, Component footer) {
        player.setPlayerListHeaderFooter(header, footer);
    }

    @Override
    public void sendActionBar(Player player, Component message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(LegacyComponentSerializer.legacySection().serialize(message)), (byte)2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void updatePlayerCommands(Player player) {
        // TODO: figure out how to do this in 1.8
    }

    @Override
    public void sendTitle(Player player, Title title) {
        try (final BukkitAudiences adv = adventure()) {
            adv.player(player).showTitle(title);
        } catch (IllegalStateException ignored) {
        }
    }

}
