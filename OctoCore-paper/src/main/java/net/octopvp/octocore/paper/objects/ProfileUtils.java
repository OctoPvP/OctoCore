package net.octopvp.octocore.paper.objects;

public final class ProfileUtils {
    private ProfileUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean doesProfileEqualsProfile(PlayerData profile1, PlayerData profile2){
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
        if(profile1.getNick() != profile2.getNick())
            return false;
        return true;
    }
    public static boolean doesProfileEqualsProfileIgnoreMessages(PlayerData profile1, PlayerData profile2){
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
        if(profile1.getNick() != profile2.getNick())
            return false;
        return true;
    }
}
