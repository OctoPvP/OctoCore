package net.octopvp.octocore.common.rank;

import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
public class LuckpermsManager {
    @Getter
    private static LuckPerms luckPerms;
    private static HashMap<String, String> colors = new HashMap<>();
    public static void init(){
        luckPerms = LuckPermsProvider.get();
        colors.put("red", "&c");
        colors.put("aqua", "&b");
        colors.put("yellow", "&e");
        colors.put("dred", "&4");
        colors.put("gold", "&6");
        colors.put("dgreen", "&2");
        colors.put("daqua", "&3");
        colors.put("dblue", "&1");
        colors.put("blue", "&9");
        colors.put("lpurple", "&d");
        colors.put("purple", "&5");
        colors.put("dpurple", "&5");
        colors.put("white", "&f");
        colors.put("gray", "&7");
        colors.put("dgray", "&8");
        colors.put("black", "&0");
    }
    public static User getUser(UUID uuid){
        return getLuckPerms().getUserManager().getUser(uuid);
    }
    public static String getPrefix(UUID uuid){
        String prefix = getUser(uuid).getCachedData().getMetaData().getPrefix();
        return (prefix == null || prefix == "null")?"" : prefix;
    }

    /**
     * may cause some lag
     * @param uuid
     * @return
     */
    public static String getMainColor(UUID uuid){
        String color = null;
        if(hasPermission(uuid, "color.red"))
            return "&c";
        for (String s : colors.keySet()) {
            if(hasPermission(uuid, "color." + s)){
                color = colors.get(s);
            }
        }
        if(color == null || color == "" || color == "null")
            return "&7";
        else return color;
    }
    public static boolean hasPermission(User user, String permission) {
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
    public static boolean hasPermission(UUID user, String permission) {
        return getUser(user).getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}
