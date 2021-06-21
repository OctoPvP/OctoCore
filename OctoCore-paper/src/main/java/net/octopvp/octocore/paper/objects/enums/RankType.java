package net.octopvp.octocore.paper.objects.enums;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.octopvp.octocore.paper.manager.impl.LuckpermsManager;

import java.util.UUID;

public enum RankType {
    PLAYER,STAFF,ADMIN;

    public static RankType getRankType(UUID uuid){
        return getFromLpUser(LuckpermsManager.getLuckPerms().getUserManager().getUser(uuid));
    }
    public static RankType getFromLpGroup(Group group){
        for (Node node : group.getNodes()) {
            String permission = node.getKey();
            if(permission.startsWith("ranktype.")){
                return RankType.valueOf(permission.substring(9).toUpperCase());
            }
        }
        return RankType.PLAYER;
    }
    public static RankType getFromLpUser(User user){
        for (Node node : LuckPermsProvider.get().getGroupManager().getGroup(user.getPrimaryGroup()).getNodes()) {
            String permission = node.getKey();
            if(permission.startsWith("ranktype.")){
                return RankType.valueOf(permission.substring(9).toUpperCase());
            }
        }
        return RankType.PLAYER;
    }
    public static RankType getFromString(String node){
        RankType ret = RankType.valueOf(node.substring(9));
        if(ret != null)
            return ret;
        return RankType.PLAYER;
    }
}
