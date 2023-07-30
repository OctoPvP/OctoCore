package net.octopvp.octocore.v1_8;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.octopvp.octocore.core.BukkitServerImplementation;
import net.octopvp.octocore.core.module.impl.scoreboard.DefaultStringScoreboardHandler;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BukkitServerImpl1_8 implements BukkitServerImplementation {
    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

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

}
