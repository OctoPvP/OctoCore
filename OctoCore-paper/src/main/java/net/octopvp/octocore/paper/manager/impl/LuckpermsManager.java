package net.octopvp.octocore.paper.manager.impl;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckpermsManager extends Manager {
    private static LuckPerms luckPerms;
    @Getter
    private static HashMap<String, String> colors = new HashMap<>();
    @Getter
    private static HashMap<String, String> colorReversed = new HashMap<>();
    private static RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
    @Override
    public void init(OctoCore plugin) {
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
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
        for(Map.Entry<String, String> entry : colors.entrySet()){
            colorReversed.put(entry.getValue(), entry.getKey());
        }

    }
    public static User getUser(UUID uuid){
        if(uuid == null)
            Logger.debug("UUID is null");
        return getLuckPerms().getUserManager().getUser(uuid);
    }
    public static String getNodeString(UUID uuid){
        PlayerData playerData = PlayerManager.getProfile(uuid);
        if(playerData.isCustomColorEnabled()){
            if(playerData.getCustomColor() == null)
                return "gray";
            return getColorReversed().get(playerData.getCustomColor());
        }
        String color = "gray";
        for(Node node : getLuckPerms().getGroupManager().getGroup(getUser(uuid).getPrimaryGroup()).getNodes()){
            if(node.getKey().startsWith("color.")){
                color = node.getKey().replace("color.","");
            }
        }
        if(color == null || color == "" || color == "null")
            color = "gray";
        return color;
    }
    public static String getPrefix(UUID uuid){
        String prefix = getUser(uuid).getCachedData().getMetaData().getPrefix();
        return (prefix == null || prefix == "null")?"" : prefix;
    }

    /**
     * USE THIS
     * Also checks playerdata
     * @param uuid
     * @return
     */
    public static String getMainColor(UUID uuid){
        PlayerData playerData = PlayerManager.getProfile(uuid);
        if(playerData.isCustomColorEnabled()){
            if(playerData.getCustomColor() == null)
                return CC.GRAY;
            return playerData.getCustomColor() + "";
        }
        String color = getMainColor(luckPerms.getGroupManager().getGroup(getUser(uuid).getPrimaryGroup()));
        if(color == CC.GRAY){
            for (Node node : getUser(uuid).getNodes()){
                if(node.getKey().startsWith("color.")){
                    color = colors.get(node.getKey().replace("color.",""));
                }
            }
            if(color == null || color == "" || color == "null")
                color = CC.GRAY;
            return color;
        }
        return color;
    }
    public static String getMainColor(Group group){
        String color = CC.GRAY;
        for(Node node : group.getNodes()){
            if(node.getKey().startsWith("color.")){
                color = colors.get(node.getKey().replace("color.",""));
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

    public static CompletableFuture<User> loadOfflineUser(UUID uuid){
        CompletableFuture<User> userFuture = luckPerms.getUserManager().loadUser(uuid);
        return userFuture;
    }

    /**
     *
     * @param uuid
     * @param node
     * @return
     */
    @Deprecated //triggers lp data update event
    public static CompletableFuture<Boolean> hasPermissionOffline(UUID uuid,String node){
        Logger.debug(uuid.toString());
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        loadOfflineUser(uuid).thenAcceptAsync(user -> completableFuture.complete(user.getCachedData().getPermissionData().checkPermission(node).asBoolean()));
        return completableFuture;
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    @Override
    public void disable() {}
}
