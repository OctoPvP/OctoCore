package net.octopvp.octocore.paper.objects;

import com.google.gson.annotations.SerializedName;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.LuckpermsManager;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.VaultManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerProfile {
    private UUID uuid;
    @SerializedName("_id")
    private String _id; //for mongodb _id field (quick and dirty way)
    private boolean frozen,nicked = false;
    private long coins = 0,lastLoaded,lastLogin,xp = 0,/**playtime in minutes*/playTime = 0;
    private String nick, prefix, mainColor,lastKnownName,nickPrefix,nickColor,name = lastKnownName,server;
    private transient String lastMessage;
    private List<String> metaDataList = new ArrayList<>();
    private ConcurrentHashMap<String,String> metaData = new ConcurrentHashMap<>();

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
    public PlayerProfile(){}
    public void loadPlayerData(){
    }
    public String getFormattedName(boolean nicked){
        return (nicked ? nickPrefix : prefix) + (nicked ? nickColor : mainColor) + (nicked ? nick : lastKnownName);
    }
    public String getCurrentPrefix(){
        return (isNicked() ? nickPrefix : prefix);
    }
    public String getCurrentColor(){
        return (isNicked() ? nickColor : mainColor);
    }
    public void refresh(){
        OctoCore.getInstance().getSetupManager().getPlayerManager().refreshProfile(this);
    }
}