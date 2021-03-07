package net.octopvp.octocore.paper.player;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.rank.LuckpermsManager;
import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.Bukkit;

import java.util.UUID;

@Getter
@Setter
public class OctoPlayerProfile {
    private UUID uuid;
    private PlayerData data;
    private boolean frozen;
    private long xp;
    long coins;
    private String lastMessage,nick,prefix,mainColor;
    private boolean nicked;

    public OctoPlayerProfile(UUID uuid){
        this.uuid = uuid;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OctoCorePaper.getInstance(), new Runnable() {
            @Override
            public void run() {
                setPrefix(LuckpermsManager.getPrefix(uuid));
                setMainColor(LuckpermsManager.getMainColor(uuid));
            }
        },0l,20l);
    }

}
