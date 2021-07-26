package net.octopvp.octocore.paper.utils.item;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class WoolUtils {
    public static final ArrayList<ChatColor> woolColors = new ArrayList<>(Arrays.asList(ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE,
            ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY,
            ChatColor.GRAY, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.RESET,
            ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.BLACK));

    public static int convertChatColorToWoolData(ChatColor color) {
        if(color == ChatColor.DARK_RED) color = ChatColor.RED;
        if (color == ChatColor.DARK_BLUE) color = ChatColor.BLUE;

        return WoolUtils.woolColors.indexOf(color);
    }
    public static int convertStringCCToWoolData(String cc){
        AtomicReference<ChatColor> color = new AtomicReference<>();
        if(cc == ChatColor.DARK_RED.toString()) color.set(ChatColor.RED);
        if (cc == ChatColor.DARK_BLUE.toString()) color.set(ChatColor.BLUE);
        woolColors.forEach(color1->{
            if (color1.toString() == cc)
                color.set(color1);
        });
        return WoolUtils.woolColors.indexOf(color.get());
    }
    public static ItemStack chatColorToWoolItem(ChatColor color){
        return new ItemStack(Material.WOOL,1, (short) convertChatColorToWoolData(color));
    }
    public static ItemStack woolColor(DyeColor color){
        return new ItemStack(Material.WOOL,1,color.getData());
    }
}
