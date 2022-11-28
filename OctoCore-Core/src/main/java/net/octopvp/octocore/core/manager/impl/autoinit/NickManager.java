package net.octopvp.octocore.core.manager.impl.autoinit;

import com.mojang.authlib.GameProfile;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NickManager extends Manager {
    public static void disguise(Player player) {
        GameProfile gameProfile = ((CraftPlayer) player).getProfile();
        CraftPlayer cplayer = (CraftPlayer) player;
        //gameProfile.getProperties().
    }

    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
}
