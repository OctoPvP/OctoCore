package net.octopvp.octocore.common.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class CC {
    public static final String
            WHITE = ChatColor.WHITE.toString(),
            GREEN = ChatColor.GREEN.toString(),
            D_GREEN = ChatColor.DARK_GREEN.toString(),
            D_BLUE = ChatColor.DARK_BLUE.toString(),
            RED = ChatColor.RED.toString(),
            D_RED = ChatColor.DARK_RED.toString(),
            GRAY = ChatColor.GRAY.toString(),
            D_GRAY = ChatColor.DARK_GRAY.toString(),
            YELLOW = ChatColor.YELLOW.toString(),
            GOLD = ChatColor.GOLD.toString(),
            AQUA = ChatColor.AQUA.toString(),
            D_AQUA = ChatColor.DARK_AQUA.toString(),
            BLUE = ChatColor.BLUE.toString(),
            PINK = ChatColor.LIGHT_PURPLE.toString(),
            PURPLE = ChatColor.DARK_PURPLE.toString(),
            BLACK = ChatColor.BLACK.toString(),
            B = ChatColor.BOLD.toString(),
            I = ChatColor.ITALIC.toString(),
            U = ChatColor.UNDERLINE.toString(),
            S = ChatColor.STRIKETHROUGH.toString(),
            R = ChatColor.RESET.toString(),
            BOLD = ChatColor.BOLD.toString(),
            ITALIC = ChatColor.ITALIC.toString(),
            UNDERLINE = ChatColor.UNDERLINE.toString(),
            STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString(),
            RESET = ChatColor.RESET.toString(),
            PRIMARY = ChatColor.AQUA.toString(),
            ACCENT = ChatColor.DARK_AQUA.toString(),
            SPLITTER = "\u2503",
            SCOREBOARD_SEPARATOR = GRAY + S + "---------------------",
            SCOREBOARD_IP_SEPARATOR = GRAY + S + "--",
            SEPARATOR = GRAY + S + "-------------------------------------",
            BULLET = "\u2022",
            DOT = BULLET,
            NEWLINE = "\n",
            NL = "\n",
            ARROW_RIGHT = "\u00BB",
            ARROW_LEFT = "\u00AB",
            X = "\u2718",
            CHECK = "\u2714",
            SELECTOR_ARROW = "\u25b8";

    public static String
            MAIN = AQUA,
            SECONDARY = D_AQUA,
            VALUE = GRAY;

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translate(List<String> input) {
        return input.stream().map(CC::translate).collect(Collectors.toList());
    }

    public static String strip(String in) {
        return ChatColor.stripColor(in);
    }
}
