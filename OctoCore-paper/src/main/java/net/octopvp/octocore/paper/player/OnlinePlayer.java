package net.octopvp.octocore.paper.player;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.paper.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class OnlinePlayer {
    private UUID uuid;
    private Player player;
    private PlayerData pdata;
    private OctoPlayerProfile profile;
    public OnlinePlayer(UUID uuid){
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.pdata = null;
        this.profile = PlayerManager.getProfile(uuid);
    }
}
