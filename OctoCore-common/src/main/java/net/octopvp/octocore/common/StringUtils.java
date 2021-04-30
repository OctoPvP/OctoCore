package net.octopvp.octocore.common;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.octopvp.octocore.common.util.reflection.ReflectUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StringUtils {
    public static String arraytoString(String[] args){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < args.length; i++) {
            sb.append(" " + args[i]);
        }
        return sb.toString();
    }
    public static String[] stringToArray(String input,String delim){
        return input.split(delim);
    }
    public static String replaceIgnoreCase(String what_to_replace,String replace_to,String input){
        return input.replaceAll("(?i)" + what_to_replace,replace_to);
    }
    public static boolean isUuid(String s){
        return UUID.fromString(s).toString() == s;
    }
    public static List<String> getListFromString(String source) {
        if (source.equals("Empty")) return new ArrayList<>();

        return Arrays.stream(source.split(", ")).map(s -> s.replace("#%&$", ",")).collect(Collectors.toList());
    }

    public static String convertFirstUpperCase(String source) {
        return source.substring(0, 1).toUpperCase() + source.substring(1);
    }
    public static Object getTime(int seconds) {
        if (seconds < 60) {
            return seconds + "seconds";
        }

        int minutes = seconds / 60;
        int s = 60 * minutes;
        int secondsLeft = seconds - s;

        if (minutes < 60) {
            if (secondsLeft > 0) {
                return String.valueOf(minutes + "minutes " + secondsLeft + "seconds");
            }

            return String.valueOf(minutes + "minutes");
        }

        if (minutes < 1440) {
            String time = "";

            int hours = minutes / 60;
            time = hours + "hours";
            int inMins = 60 * hours;
            int leftOver = minutes - inMins;

            if (leftOver >= 1) {
                time = time + " " + leftOver + "minutes";
            }

            if (secondsLeft > 0) {
                time = time + " " + secondsLeft + "seconds";
            }

            return time;
        }
        String time = "";
        int days = minutes / 1440;
        time = days + "days";
        int inMins = 1440 * days;
        int leftOver = minutes - inMins;

        if (leftOver >= 1) {
            if (leftOver < 60) {
                time = time + " " + leftOver + "minutes";
            } else {
                int hours = leftOver / 60;
                time = time + " " + hours + "hours";
                int hoursInMins = 60 * hours;
                int minsLeft = leftOver - hoursInMins;

                if (leftOver >= 1) {
                    time = time + " " + minsLeft + "minutes";
                }
            }
        }

        if (secondsLeft > 0) {
            time = time + " " + secondsLeft + "seconds";
        }

        return time;
    }
    private static long convert(int value, char unit) {
        switch (unit) {

            case 'y': {
                return value * TimeUnit.DAYS.toMillis(365L);
            }

            case 'M': {
                return value * TimeUnit.DAYS.toMillis(30L);
            }

            case 'd': {
                return value * TimeUnit.DAYS.toMillis(1L);
            }

            case 'h': {
                return value * TimeUnit.HOURS.toMillis(1L);
            }

            case 'm': {
                return value * TimeUnit.MINUTES.toMillis(1L);
            }

            case 's': {
                return value * TimeUnit.SECONDS.toMillis(1L);
            }

            default: {
                return -1L;
            }
        }
    }
    public static String getEnchantment(String name) {
        String enchant = name;

        if (name.equalsIgnoreCase("sharp") || name.equalsIgnoreCase("sharpness")) {
            enchant = "DAMAGE_ALL";
        }

        if (name.equalsIgnoreCase("ff") || name.equalsIgnoreCase("featherfalling")) {
            enchant = "FEATHER_FALLING";
        }

        if (name.equalsIgnoreCase("fire") || name.equalsIgnoreCase("fireaspect")) {
            enchant = "FIRE_ASPECT";
        }

        if (name.equalsIgnoreCase("kb") || name.equalsIgnoreCase("knock")) {
            enchant = "KNOCKBACK";
        }

        if (name.equalsIgnoreCase("smi") || name.equalsIgnoreCase("smite")) {
            enchant = "DAMAGE_UNDEAD";
        }

        if (name.equalsIgnoreCase("bane") || name.equalsIgnoreCase("baneof") || name.equalsIgnoreCase("baneofarthropods")) {
            enchant = "DAMAGE_ARTHROPODS";
        }

        if (name.equalsIgnoreCase("prot") || name.equalsIgnoreCase("protection")) {
            enchant = "PROTECTION_ENVIRONMENTAL";
        }

        if (name.equalsIgnoreCase("fire") || name.equalsIgnoreCase("fireprot") || name.equalsIgnoreCase("fireprotection")) {
            enchant = "PROTECTION_FIRE";
        }

        if (name.equalsIgnoreCase("blast") || name.equalsIgnoreCase("blastprot") || name.equalsIgnoreCase("blastprotection")) {
            enchant = "PROTECTION_EXPLOSIONS";
        }

        if (name.equalsIgnoreCase("proj") || name.equalsIgnoreCase("projprot") || name.equalsIgnoreCase("projectileprotection")) {
            enchant = "PROTECTION_PROJECTILE";
        }

        if (name.equalsIgnoreCase("loot") || name.equalsIgnoreCase("looting")) {
            enchant = "LOOT_BONUS_MOBS";
        }

        if (name.equalsIgnoreCase("fort") || name.equalsIgnoreCase("fortune")) {
            enchant = "LOOT_BONUS_BLOCKS";
        }

        if (name.equalsIgnoreCase("silk") || name.equalsIgnoreCase("silktouch")) {
            enchant = "SILK_TOUCH";
        }

        if (name.equalsIgnoreCase("pow") || name.equalsIgnoreCase("power")) {
            enchant = "ARROW_DAMAGE";
        }

        if (name.equalsIgnoreCase("pun") || name.equalsIgnoreCase("punch")) {
            enchant = "ARROW_KNOCKBACK";
        }

        if (name.equalsIgnoreCase("fla") || name.equalsIgnoreCase("flame")) {
            enchant = "ARROW_FIRE";
        }

        if (name.equalsIgnoreCase("inf") || name.equalsIgnoreCase("infinity")) {
            enchant = "ARROW_INFINITE";
        }

        if (name.equalsIgnoreCase("unb") || name.equalsIgnoreCase("unbreaking")) {
            enchant = "DURABILITY";
        }

        if (name.equalsIgnoreCase("eff") || name.equalsIgnoreCase("efficiency")) {
            enchant = "DIG_SPEED";
        }

        return enchant.toUpperCase();
    }
    public static String buildString(String[] args, int start) {
        if (start >= args.length) return "";
        return ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }
    public static String replacePlaceholders(final String str, final String... replace){
        int i = 0;
        String finalReturn = str;
        for (String s : replace) {
            i++;
            String toReplace = "%" + i;
            finalReturn = finalReturn.replace(toReplace,s);
        }
        return finalReturn;
    }
    public static StringBuilder appendRandomChatColors(StringBuilder sb, int howmanyper,int howmanytimes){
        List<ChatColor> colors = Arrays.asList(ChatColor.ALL_CHATCOLORS);
        for (int i = 0; i < howmanytimes; i++) {
            sb.append("\n");
            for (int i1 = 0; i1 < howmanyper; i1++) {
                sb.append(colors.indexOf(RNG.getRandomInt(0,ChatColor.ALL_CHATCOLORS.length)));
            }
        }
        return sb;
    }
    public static List<String> getRandomChatColorsAsList(int howmanyper,int howmanytimes){
        List<ChatColor> colors = Arrays.asList(ChatColor.ALL_CHATCOLORS);
        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < howmanytimes; i++) {
            StringBuilder sb = new StringBuilder();
            for (int i1 = 0; i1 < howmanyper; i1++) {
                sb.append(colors.indexOf(RNG.getRandomInt(0,ChatColor.ALL_CHATCOLORS.length)) + "");
            }
            ret.add(sb.toString());
        }
        return ret;
    }
    public static String[] getRandomChatColors(int howmanyper,int howmanytimes){
        return (String[]) getRandomChatColorsAsList(howmanyper, howmanytimes).toArray();
    }
}
