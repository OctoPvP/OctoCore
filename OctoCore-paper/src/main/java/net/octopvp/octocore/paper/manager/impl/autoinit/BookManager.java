package net.octopvp.octocore.paper.manager.impl.autoinit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.utils.book.BookUtil;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BookManager extends Manager {
    @Override
    public void init(OctoCore plugin) {
        TextComponent click = new TextComponent("\n\n" + CC.GREEN + "Click " + CC.U + "Here" + CC.R + CC.GREEN + " to \n" + CC.GREEN + "download lunar client");
        click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://lunarclient.com/download"));
        click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.GREEN + "Click here to go to lunarclient.com").create()));
        unsupportedVersionTC = new TextComponent(new TextComponent(CC.D_RED + CC.BOLD + "Warning!\n\n" + CC.RED + "You are on a " + CC.RED + CC.U + "UNSUPPORTED " + CC.R + CC.RED + "version\n" + CC.RED + "of minecraft.\n\n" + CC.GREEN + "Please use minecraft \n" + CC.GREEN + "version " + CC.U + "1.8.9"),click);
        unsupportedVersionBook = BookUtil.writtenBook().pages(new BookUtil.PageBuilder().add(unsupportedVersionTC).build()).build();
    }

    @Override
    public void disable() {
    }
    private static TextComponent unsupportedVersionTC;
    private static ItemStack unsupportedVersionBook;
    public static void showUnsupportedVerBook(Player player){
        BookUtil.openBook(player,unsupportedVersionBook);
    }
}
