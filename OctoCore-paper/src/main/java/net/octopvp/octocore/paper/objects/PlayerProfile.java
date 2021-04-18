package net.octopvp.octocore.paper.objects;

import com.google.gson.annotations.SerializedName;
import net.octopvp.octocore.common.rank.LuckpermsManager;
import net.octopvp.octocore.paper.manager.VaultManager;
import net.octopvp.octocore.paper.utils.tab.tablist.TableTabList;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerProfile {
    private UUID uuid;
    @SerializedName("_id")
    private String _id; //for mongodb _id field (quick and dirty way)
    private boolean frozen,nicked;
    private long coins = 0,lastLoaded,lastLogin,xp = 0,/**playtime in minutes*/playTime = 0;
    private String nick, prefix, mainColor,lastKnownName,nickPrefix,name = lastKnownName;
    private transient String lastMessage;
    private transient Document document;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        if(isNicked()){
            this.prefix = this.nickPrefix;
        }else{
            //DO NOT USE PlayerManager#getPrefix as it has to load the profile (this)
            this.prefix = (Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultManager.isChatHookEnabled()) ?
                    VaultManager.getChat().getPlayerPrefix(Bukkit.getPlayer(uuid)) :
                    LuckpermsManager.getPrefix(uuid);
        }
        this.mainColor = LuckpermsManager.getMainColor(uuid);
        this.lastLoaded = System.currentTimeMillis();
        this.lastKnownName = Bukkit.getPlayer(uuid).getName();
        this.name = lastKnownName;
        this._id = uuid.toString();
    }
}