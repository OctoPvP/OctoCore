package net.octopvp.octocore.paper.manager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckpermsManager implements Manager{
    private static LuckPerms luckPerms;
    private static HashMap<String, String> colors = new HashMap<>();
    @Override
    public void init(OctoCore plugin) {
        luckPerms = LuckPermsProvider.get();
    }
    static{
        colors.put("red", CC.RED);
        colors.put("aqua", CC.AQUA);
        colors.put("yellow", CC.YELLOW);
        colors.put("dred", CC.D_RED);
        colors.put("gold", CC.GOLD);
        colors.put("dgreen",CC.D_GREEN);
        colors.put("daqua", CC.D_AQUA);
        colors.put("dblue", CC.D_BLUE);
        colors.put("blue", CC.BLUE);
        colors.put("lpurple", CC.PINK);
        colors.put("pink",CC.PINK);
        colors.put("purple", CC.PURPLE);
        colors.put("dpurple", CC.PURPLE);
        colors.put("white", CC.WHITE);
        colors.put("gray", CC.GRAY);
        colors.put("dgray", CC.D_GRAY);
        colors.put("black", CC.BLACK);
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
        /*
        if(hasPermission(uuid, "color.red"))
            return CC.RED;
        for (String s : colors.keySet()) {
            if(hasPermission(uuid, "color." + s)){
                color = colors.get(s);
            }
        }
         */
        for (Node node : getUser(uuid).getNodes()) {
            if(node.getKey().startsWith("color.")){
                color = colors.get(node.getKey().replace("color.",""));
                break;
            }
        }
        if(color == null || color == "" || color == "null")
            color = CC.GRAY;
        return color;
    }
    public static boolean hasPermission(User user, String permission) {
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
    public static boolean hasPermission(UUID user, String permission) {
        return getUser(user).getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public static LuckPerms getLuckPerms() {
        return LuckpermsManager.luckPerms;
    }

    @Override
    public void disable(OctoCore plugin) {

    }
}
