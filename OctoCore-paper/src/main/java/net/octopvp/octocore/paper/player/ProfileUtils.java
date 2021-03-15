package net.octopvp.octocore.paper.player;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProfileUtils {
    public static boolean doesProfileEqualsProfile(PlayerProfile profile1, PlayerProfile profile2){
        if(profile1.getPlayer() != profile2.getPlayer())
            return false;
        if(profile1.getUuid() != profile2.getUuid())
            return false;
        if (profile1.getCoins() != profile2.getCoins())
            return false;
        if(profile1.isFrozen() != profile2.isFrozen())
            return false;
        if(profile1.getXp() != profile2.getXp())
            return false;
        if(profile1.getLastMessage() != profile2.getLastMessage())
            return false;
        if(profile1.getPrefix() != profile2.getPrefix())
            return false;
        if(profile1.getMainColor() != profile2.getMainColor())
            return false;
        if(profile1.getData() != profile2.getData())
            return false;
        if(profile1.getTab() != profile2.getTab())
            return false;
        if(profile1.getNick() != profile2.getNick())
            return false;
        return true;
    }
    public static boolean doesProfileEqualsProfileIgnoreMessages(PlayerProfile profile1, PlayerProfile profile2){
        if(profile1.getPlayer() != profile2.getPlayer())
            return false;
        if(profile1.getUuid() != profile2.getUuid())
            return false;
        if (profile1.getCoins() != profile2.getCoins())
            return false;
        if(profile1.isFrozen() != profile2.isFrozen())
            return false;
        if(profile1.getXp() != profile2.getXp())
            return false;
        if(profile1.getPrefix() != profile2.getPrefix())
            return false;
        if(profile1.getMainColor() != profile2.getMainColor())
            return false;
        if(profile1.getData() != profile2.getData())
            return false;
        if(profile1.getTab() != profile2.getTab())
            return false;
        if(profile1.getNick() != profile2.getNick())
            return false;
        return true;
    }
}
