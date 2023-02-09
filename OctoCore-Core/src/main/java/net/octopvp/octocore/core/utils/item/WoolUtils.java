package net.octopvp.octocore.core.utils.item;

import net.octopvp.agile.util.XMaterial;
import net.octopvp.octocore.core.utils.chat.ChatUtil;
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
    public static final ArrayList<XMaterial> woolMaterials = new ArrayList<>(Arrays.asList(XMaterial.WHITE_WOOL, XMaterial.ORANGE_WOOL, XMaterial.MAGENTA_WOOL,
            XMaterial.LIGHT_BLUE_WOOL, XMaterial.YELLOW_WOOL, XMaterial.LIME_WOOL, XMaterial.PINK_WOOL, XMaterial.GRAY_WOOL,
            XMaterial.LIGHT_GRAY_WOOL, XMaterial.CYAN_WOOL, XMaterial.PURPLE_WOOL, XMaterial.BLUE_WOOL, XMaterial.BROWN_WOOL,
            XMaterial.GREEN_WOOL, XMaterial.RED_WOOL, XMaterial.BLACK_WOOL));

    public static int convertChatColorToWoolData(ChatColor color) {
        if (color == ChatColor.DARK_RED) color = ChatColor.RED;
        if (color == ChatColor.DARK_BLUE) color = ChatColor.BLUE;
        if (color == ChatColor.RESET) color = ChatColor.WHITE;

        return WoolUtils.woolColors.indexOf(color);
    }
    public static XMaterial convertChatColorToWoolMaterial(ChatColor color) {
        return woolMaterials.get(convertChatColorToWoolData(color));
    }

    public static int convertChatColorToWoolData(net.octopvp.octocore.common.util.ChatColor color) {
        return convertChatColorToWoolData(ChatUtil.convertChatColor(color));
    }

    public static int convertStringCCToWoolData(String cc) {
        AtomicReference<ChatColor> color = new AtomicReference<>();
        if (cc == ChatColor.DARK_RED.toString()) color.set(ChatColor.RED);
        if (cc == ChatColor.DARK_BLUE.toString()) color.set(ChatColor.BLUE);
        if (cc == ChatColor.WHITE.toString()) color.set(ChatColor.WHITE);
        woolColors.forEach(color1 -> {
            if (color1.toString() == cc)
                color.set(color1);
        });
        return WoolUtils.woolColors.indexOf(color.get());
    }
}
