package net.octopvp.octocore.paper.objects;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.HashedAddress;
import net.octopvp.octocore.common.object.WorldTime;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.LuckpermsManager;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.manager.impl.VaultManager;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerData {
    //TODO set defaults for this so theres no errors when using/loading old data from older updates (idk if this makes sense lol)
    private UUID uuid;
    @SerializedName("_id")
    private String _id; //for mongodb _id field (quick and dirty way)
    private boolean frozen,nicked = false,authEnabled = false, vanished = false, joinVanished = false,customColorEnabled = false;
    private CC customColor;
    private transient boolean fullyJoined = false;
    private long coins = 0,lastLoaded,lastLogin,xp = 0,firstJoin = System.currentTimeMillis();
    private String nick, prefix, mainColor,lastKnownName = "<unknown>",nickPrefix,nickColor,name = lastKnownName,server,authSecret,lastSeenServer = "Unknown",rankName = "default";
    private HashedAddress lastAuthedIp = new HashedAddress(""),lastSeenIp = new HashedAddress("");
    private transient String lastMessage; //only applies to this server for spam prot (maybe :))
    private List<String> metaDataList = new ArrayList<>();
    private Map<String,String> metaData = new ConcurrentHashMap<>();
    private RankType rankType = RankType.PLAYER; //player's rank type, defaults to PLAYER (not meant for permission managment)
    private WorldTime worldTime = WorldTime.DAY;
    private PlayerTag tag = null,nickTag = null;
    private int /**playtime in seconds, dont need to make it an long since 2.1b seconds is 66 years*/playTime = 0;
    private List<PlayerTag> allowedTags = new ArrayList<>();
    private UUID nickUUID;


    private boolean staffChatAlerts = true, adminChatAlerts = true, reportAlerts = true;
    private boolean staffChat = false,adminChat = false;
    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.lastLoaded = System.currentTimeMillis();
        this.lastKnownName = Bukkit.getPlayer(uuid).getName();
        this.name = lastKnownName;
        this._id = uuid.toString();
        Player player = Bukkit.getPlayer(uuid);
        this.lastSeenIp = new HashedAddress(player.getAddress().getHostName());
        setupLpThings();
    }
    public void setupLpThings(){
        Tasks.runLater(()->{
            //DO NOT USE PlayerManager#getPrefix as it has to load the profile (this)
            this.prefix = (Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultManager.isChatHookEnabled()) ?
                    VaultManager.getChat().getPlayerPrefix(Bukkit.getPlayer(uuid)) :
                    LuckpermsManager.getPrefix(uuid);
            this.mainColor = LuckpermsManager.getMainColor(uuid);
        },20l);
    }
    public void reloadLuckPermsThings(){
        setPrefix(PlayerManager.getPrefix(this));
        setMainColor(LuckpermsManager.getMainColor(uuid));
        setRankType(RankType.getRankType(uuid));
    }
    public String getFormattedName(boolean nicked){
        if (nicked)
            return CC.translate((this.isNicked() ? nickPrefix : prefix) + (this.isNicked() ? nickColor : mainColor) + " " + (this.isNicked() ? nick : lastKnownName)) + (tag != null ? " " + getTagString() : "");
        return CC.translate(prefix + mainColor + " " + lastKnownName) + (tag != null ? " " + getTagString() : "");
    }
    public String getCurrentPrefix(){
        return (isNicked() ? nickPrefix : prefix);
    }
    public String getCurrentColor(){
        return (isNicked() ? nickColor : getActualMainColor());
    }
    public String getActualMainColor(){
        return (customColorEnabled && customColor != null ? customColor.toString() : mainColor);
    }
    public boolean isOnline(String name) {// FIXME inverted this because its returning false even if they are online
        return OctoCore.getServerManager().getConnectedServers().stream().filter(serverData ->
                serverData.getNames().stream().map(String::toLowerCase).collect(Collectors.toList())
                        .contains(name.toLowerCase())).findFirst().orElse(null) == null;
    }
    public boolean isOnline(){
        return isOnline(name);
    }
    public void refresh(){
        OctoCore.getInstance().getPlayerManager().refreshProfile(this);
    }
    public String getTagString(){
        if (tag == null)
            return "";
        if (isNicked()){
            return CC.D_GRAY + CC.ARROW_LEFT + getNickTag().getTag() + CC.D_GRAY + CC.ARROW_RIGHT;
        }
        return CC.D_GRAY + CC.ARROW_LEFT + getTag().getTag() + CC.D_GRAY + CC.ARROW_RIGHT;
    }
    public boolean hasTag(String tagName){
        boolean a = false;
        for (PlayerTag playerTag : allowedTags) {
            if (playerTag.getName().equalsIgnoreCase(tagName)) {
                a = true;
                break;
            }
        }
        return a;
    }
    public UUID getMainSkinUUID(){
        return (isNicked() ? nickUUID : uuid);
    }
}