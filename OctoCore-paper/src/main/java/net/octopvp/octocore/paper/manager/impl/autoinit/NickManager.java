package net.octopvp.octocore.paper.manager.impl.autoinit;

import com.mojang.authlib.GameProfile;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
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
