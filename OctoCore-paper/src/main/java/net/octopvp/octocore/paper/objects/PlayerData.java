package net.octopvp.octocore.paper.objects;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.octopvp.octocore.common.object.HashedAddress;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.WorldTime;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;
import net.octopvp.octocore.common.util.permissions.PermissionReason;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.PermissionManager;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.menus.grant.GrantProcedure;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.objects.permissions.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerData {
    private static transient Plugin plugin = OctoCore.getInstance();
    //TODO set defaults for this so theres no errors when using/loading old data from older updates (idk if this makes sense lol)
    private UUID uuid;
    private double dataVersion = 0.0;
    @SerializedName("_id")
    private String _id; //for mongodb _id field (quick and dirty way)
    private boolean frozen,nicked = false,authEnabled = false, vanished = false, joinVanished = false,customColorEnabled = false;
    private String customColor;
    private transient boolean fullyJoined = false;
    private transient int lastDataSave = 0;
    private long coins = 0,lastLoaded,lastLogin,xp = 0,firstJoin = System.currentTimeMillis();
    private String nick,lastKnownName = "<unknown>",nickPrefix,nickColor,name = lastKnownName,server,authSecret,lastSeenServer = "Unknown",rankName = "default";
    private HashedAddress lastAuthedIp = new HashedAddress(""),lastSeenIp = new HashedAddress("");
    private transient String lastMessage; //only applies to this server for spam prot (maybe :))
    private List<String> metaDataList = new ArrayList<>();
    private Map<String,String> metaData = new ConcurrentHashMap<>();
    private RankType rankType = RankType.DEFAULT; //player's rank type, defaults to PLAYER (not meant for permission managment)
    private WorldTime worldTime = WorldTime.DAY;
    private PlayerTag tag = null,nickTag = null;
    private int /**playtime in seconds, dont need to make it an long since 2.1b seconds is 66 years*/playTime = 0;
    private List<PlayerTag> allowedTags = new ArrayList<>();
    private UUID nickUUID;
    private ChatColor nameColor = ChatColor.GREEN,chatColor = ChatColor.GREEN;
    private boolean nameColorBold = false, nameColorItalic = false;

    private boolean staffChatAlerts = true, adminChatAlerts = true, reportAlerts = true;
    private boolean staffChat = false,adminChat = false,build = false;

    private Set<Grant> grants = new HashSet<>();
    //private Map<String, Pair<ServerContext,Boolean>> permissions = new HashMap<>();
    private Set<Node> nodes = new HashSet<>();
    private SaveState saveState = SaveState.SAVED;
    private String s = "default";

    private transient List<LoadNote> loadNotes = new ArrayList<>();
    private transient GrantProcedure grantProcedure = null;
    private transient Map<String, PermissionResult> cachedPermissions = new ConcurrentHashMap<>();
    public PlayerData(UUID uuid,String name) {
        this.uuid = uuid;
        this.lastLoaded = System.currentTimeMillis();
        this.lastKnownName = name;
        this.name = lastKnownName;
        this._id = uuid.toString();
        Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            this.lastSeenIp = new HashedAddress(player.getAddress().getHostName());
        onLoad();
    }
    public void onLoad(Document... documents){
        this.grants.removeIf(Objects::isNull);
        this.lastLoaded = System.currentTimeMillis();
        if (cachedPermissions == null)
            cachedPermissions = new ConcurrentHashMap<>();
        if (loadNotes == null)
            loadNotes = new ArrayList<>();
        Document document = (documents.length == 1 ? documents[0] : PlayerManager.getProfileDocument(uuid));
    }
    public Node getNode(String perm){
        return nodes.stream().filter(node -> node.getPermission().equalsIgnoreCase(perm)).findFirst().orElse(null);
    }
    public boolean nodeExists(String perm){
        return getNode(perm) != null;
    }
    public void save(){
        PlayerManager.saveProfile(this);
    }
    public String getFormattedName(boolean nicked){
        if (nicked)
            return CC.translate((this.isNicked() ? nickPrefix : getHighestRank().getPrefix()) + (this.isNicked() ? nickColor : getCurrentColor()) + " " + (this.isNicked() ? nick : lastKnownName)) + (tag != null ? " " + getTagString() : "");
        return CC.translate(getHighestRank().getPrefix() + getNameColor() + " " + lastKnownName) + (tag != null ? " " + getTagString() : "");
    }
    public PlayerData addLoadNote(LoadNote note){
        this.loadNotes.add(note);
        return this;
    }
    public String getCurrentPrefix(){
        return (isNicked() ? nickPrefix : getHighestRank().getPrefix());
    }
    public String getCurrentColor(){
        return (isNicked() ? nickColor : getActualMainColor());
    }
    public String getActualMainColor(){
        return (customColor != null &&
                isCustomColorEnabled() ?
                customColor :
                getHighestRank().getColor().toString());
    }
    public String getPrefixColorOrNull(){
        return (customColor != null && isCustomColorEnabled() ? customColor : null);
    }
    public boolean isOnline(String name) {// FIXME inverted this because its returning false even if they are online
        return OctoCore.getServerManager().getConnectedServers().stream().filter(serverData ->
                serverData.getNames().stream().map(String::toLowerCase).collect(Collectors.toList())
                        .contains(name.toLowerCase())).findFirst().orElse(null) == null;
    }
    public boolean isOnlineThisServer(){
        return Bukkit.getPlayer(uuid) != null;
    }
    public boolean isOnline(){
        return isOnline(name);
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
    public boolean isCustomColorEnabled(){
        if (customColorEnabled){
            return this.hasPermission(Permission.CUSTOM_COLOR.toString());
        }return false;
    }
    public String getNameColor() {
        if (this.nameColor == null) {
            return this.getHighestRank().getDisplayColor();
        }
        if (this.isNameColorBold() && this.isNameColorItalic()) {
            return this.nameColor.toString() + org.bukkit.ChatColor.BOLD + org.bukkit.ChatColor.ITALIC;
        }
        if (this.isNameColorBold()) {
            return this.nameColor.toString() + org.bukkit.ChatColor.BOLD;
        }
        if (this.isNameColorItalic()) {
            return this.nameColor.toString() + org.bukkit.ChatColor.ITALIC;
        }
        return this.nameColor.toString();
    }
    public List<Grant> getActiveGrants() {
        return this.grants.stream().filter(grant -> !grant.hasExpired() && RankManager.getRankById(grant.getRankId()) != null).collect(Collectors.toList());
    }
    public boolean hasRank(Rank rankData) {
        for (Grant grant : this.getActiveGrants()) {
            if (grant.getRankName().equalsIgnoreCase(rankData.getName())) {
                return true;
            }
        }
        return false;
    }
    public boolean isNon(){
        return this.getHighestRank().isDefaultRank(); //F
    }
    public Rank getHighestRank(){
        return this.getActiveGrants().stream().map(Grant::getRank)
                .max(Comparator.comparingInt(Rank::getWeight)).orElse(RankManager.getDefaultRank());
    }
    public Set<Node> getFinalNodes(){
        Set<Node> nodes1 = new HashSet<>(nodes);
        for (Node finalNode : getHighestRank().getFinalNodes()) {
            if (nodes1.stream().filter(node -> node.getPermission().equals(finalNode.getPermission())).findFirst().orElse( null) == null){ //node is not manually set
                nodes1.add(finalNode);
            }
        }
        return nodes1;
    }
    public boolean hasPermission(Node node){
        return hasPermission(node.getPermission());
    }
    public boolean hasPermission(String perm){
        PermissionResult result = PermissionCalculator.hasPermissionResult(perm,getFinalNodes());
        if (result.getReason() == PermissionReason.NOT_SET)
            return getHighestRank().hasPermission(perm);
        else return result.allowed();
    }


    public void loadPerms(Player player) {
        Map<String,Boolean> bungeePermissions = new HashMap<>();
        List<Grant> currentGrants = new ArrayList<>(this.grants);
        for (Grant grant : currentGrants) {
            if (grant.hasExpired()) continue;
            Rank rankData = grant.getRank();
            if (rankData != null) {
                bungeePermissions.putAll(rankData.getEffectiveBungeePermissions());
                ArrayList<UUID> inheritances = Lists.newArrayList(rankData.getInheritedRanks());
                inheritances.forEach(inheritance -> {
                    Rank rankInheritance = RankManager.getRankById(inheritance);
                    bungeePermissions.putAll(rankInheritance.getEffectiveBungeePermissions());
                });
            }
        }

        Rank defaultRank = RankManager.getDefaultRank();
        if (defaultRank != null) {
            bungeePermissions.putAll(defaultRank.getEffectiveBungeePermissions());
            Set<UUID> inheritances = defaultRank.getInheritedRanks();
            inheritances.forEach(inheritance -> {
                Rank rankInheritance = RankManager.getRankById(inheritance);
                if (rankInheritance != null) {
                    bungeePermissions.putAll(rankInheritance.getEffectiveBungeePermissions());
                }
            });
        }
        /*
        Rank rankData = this.getHighestRank();
        if (!player.getDisplayName().equals(CC.translate(rankData.getPrefix(this.getPrefixColorOrNull()) + rankData.getColor() + (this.nameColor != null ? this.nameColor.toString() : "") + this.getName()) + CC.R)) {
            player.setDisplayName(CC.translate(rankData.getPrefix(getPrefixColorOrNull()) + rankData.getColor() + (this.nameColor != null ? this.nameColor.toString() : "") + this.getName()) + CC.R);
        }
         */
        if (!player.getDisplayName().equals(this.getDisplayName())) //TODO handle nicks
            player.setDisplayName(this.getDisplayName());

        bungeePermissions.forEach((permission,bool) -> RankManager.sendPermissionToBungee(player, player.getName(), permission, bool,"global"));
    }
    public String getPrefix(){
        return CC.translate(getHighestRank().getPrefix(this.getPrefixColorOrNull()));
    }
    public Map<String,ServerContext> getAllEffectivePermissions(){
        Map<String,ServerContext> permissions = new HashMap<>();
        List<Grant> currentGrants = new ArrayList<>(this.grants);
        for (Grant grant : currentGrants) {
            if (grant.hasExpired()) continue;
            Rank rankData = grant.getRank();
            if (rankData != null) {
                permissions.putAll(rankData.getAllEffectivePermissions());

                List<UUID> inheritances = new ArrayList<>(rankData.getInheritedRanks());
                inheritances.forEach(inheritance -> {
                    Rank rankInheritance = RankManager.getRankById(inheritance);

                    if (rankInheritance != null) {
                        permissions.putAll(rankInheritance.getAllEffectivePermissions());
                    }
                });
            }
        }
        Rank defaultRank = RankManager.getDefaultRank();
        if (defaultRank != null){
            permissions.putAll(defaultRank.getAllEffectivePermissions());
            defaultRank.getInheritedRanks().forEach(i ->{
                Rank inherited = RankManager.getRankById(i);
                if (inherited != null)
                    permissions.putAll(inherited.getAllEffectivePermissions());
            });
        }
        permissions.putAll(this.getAllSetEffectivePermissions());
        return permissions;
    }
    public String getDisplayName(){
        if (nicked)
            return CC.translate(getCurrentColor() + getNickColor() + CC.R);
        else
            return CC.translate(getCurrentColor() + getName() + CC.R);
    }
    public Map<String,ServerContext> getAllNegatedPermissions(){
        Map<String,ServerContext> permissions = new HashMap<>();
        List<Grant> currentGrants = new ArrayList<>(this.grants);
        Iterator<Grant> grantIterator = currentGrants.iterator();
        while (grantIterator.hasNext()) {
            Grant grant = grantIterator.next();
            if (grant.hasExpired()) continue;
            Rank rankData = grant.getRank();
            if (rankData != null) {
                permissions.putAll(rankData.getNegatedPermissions());
                List<UUID> inheritances = new ArrayList<>(rankData.getInheritedRanks());
                inheritances.forEach(inheritance -> {
                    Rank rankInheritance = RankManager.getRankById(inheritance);
                    if (rankInheritance != null) permissions.putAll(rankInheritance.getNegatedPermissions());
                });
            }
        }
        Rank defaultRank = RankManager.getDefaultRank();
        if (defaultRank != null){
            permissions.putAll(defaultRank.getNegatedPermissions());
            defaultRank.getInheritedRanks().forEach(i ->{
                Rank inherited = RankManager.getRankById(i);
                if (inherited != null) permissions.putAll(inherited.getNegatedPermissions());
            });
        }
        permissions.putAll(getAllSetNegatedPermissions());
        return permissions;
    }
    public Map<String,ServerContext> getAllSetEffectivePermissions(){
        Map<String,ServerContext> a = new HashMap<>();
        nodes.forEach(node ->{
            if (hasPermission(node.getPermission()))
                a.put(node.getPermission(),node.getServer());
        });
        return a;
    }
    public Map<String,ServerContext> getAllSetNegatedPermissions(){
        Map<String,ServerContext> a = new HashMap<>();
        nodes.forEach(node ->{
            if (node.isNegated())
                a.put(node.getPermission(),node.getServer());
        });
        return a;
    }
    public static enum SaveState {
        SAVED,SAVING
    }
    public static enum LoadNote {

    }
}