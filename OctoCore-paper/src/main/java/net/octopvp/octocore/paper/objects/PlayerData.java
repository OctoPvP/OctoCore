package net.octopvp.octocore.paper.objects;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.*;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;
import net.octopvp.octocore.common.util.permissions.PermissionReason;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.player.AltUpdatePacket;
import net.octopvp.octocore.paper.database.redis.packets.staff.StaffConnectPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishData;
import net.octopvp.octocore.paper.module.impl.punishments.util.Alt;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.GsonType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerData implements IPlayerData, IPunishData {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    //TODO set defaults for this so theres no errors when using/loading old data from older updates (idk if this makes sense lol)
    private UUID uuid;
    private double dataVersion = 0.0;

    private long lastLoaded, lastLogin, xp = 0, firstJoin = System.currentTimeMillis(), lastSave = System.currentTimeMillis(), lastSeen = -1;
    private String nick, customColor, lastKnownName = "<unknown>", nickPrefix, nickColor, name = lastKnownName;
    private String lowerName = name.toLowerCase(), server, authSecret, lastSeenServer = "Unknown", rankName = "default";
    private String lastAuthedIp = "", lastSeenIp = "", address, lastServerOn = "Unknown";
    private List<String> metaDataList = new ArrayList<>();
    private Map<String, String> metaData = new ConcurrentHashMap<>();
    private WorldTime worldTime = WorldTime.DAY;
    private UUID tagID = null, nickTagID = null, nickUUID;
    private int /*playtime in seconds, dont need to make it an long since 2.1b seconds is 66 years*/
            playTime = 0, coins;
    private HashSet<UUID> allowedTagsID = new HashSet<>();
    private ChatColor nameColor = ChatColor.GREEN;
    private boolean nameColorBold = false, nameColorItalic = false, staffChatAlerts = true, adminChatAlerts = true;
    private boolean reportAlerts = true, staffChat = false, adminChat = false, build = false;
    private boolean frozen, nicked = false, authEnabled = false, vanished = false, joinVanished = false;
    private boolean customColorEnabled = false, savingOnQuit = false, loaded = false, fullJoined = false;
    private boolean joinAlert = false, socialSpy = false;

    private ArrayList<Grant> grants = new ArrayList<>();
    //private Map<String, Pair<ServerContext,Boolean>> permissions = new HashMap<>();
    private List<Node> nodes = new ArrayList<>();
    private List<Node> bungeePerms = new ArrayList<>();

    private transient boolean fullyJoined = false, op = false;
    private transient int lastDataSave = 0;
    private transient String lastMessage; //only applies to this server for spam prot (maybe :))
    private transient Set<PlayerTag> allowedTags;
    private transient List<Punishment> punishmentsExecuted = new ArrayList<>();
    private transient List<LoadNote> loadNotes = new ArrayList<>();
    private transient GrantProcedure grantProcedure = null;
    private transient Map<String, PermissionResult> cachedPermissions = new ConcurrentHashMap<>();

    private UUID lastMessaged = null;

    private PunishData punishData = new PunishData(this);
    private Collection<Alt> alts = new ArrayList<>();
    private List<String> addresses = new ArrayList<>();

    private List<UUID> ignoredPlayers = new ArrayList<>();

    private MessageSettings messageSettings = new MessageSettings();

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.lastLoaded = System.currentTimeMillis();
        this.lastKnownName = name;
        this.name = lastKnownName;
        this.lowerName = name.toLowerCase();
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) this.lastSeenIp = player.getAddress().getHostName();
    }

    public void load() {
        load(null);
    }

    public void load(@Nullable Document document) {
        if (document == null) {
            document = PlayerManager.getInstance().getProfileDocument(uuid);
        }
        if (document == null) {
            return;
        }
        Gson gson = OctoCore.getGson();
        this.name = requestName();

        this.lowerName = name.toLowerCase();
        this.lastLoaded = System.currentTimeMillis();
        this.grants = gson.fromJson(document.getString("grants"), GsonType.GRANT);
        this.grants.removeIf(Objects::isNull);
        this.dataVersion = getDouble(document, "dataVersion");
        this.frozen = document.getBoolean("frozen");
        this.nicked = document.getBoolean("nicked");
        this.authEnabled = document.getBoolean("authEnabled");
        this.vanished = document.getBoolean("vanished");
        this.joinVanished = document.getBoolean("joinVanished");
        this.customColorEnabled = document.getBoolean("customColorEnabled");
        this.customColor = document.getString("customColor");
        this.coins = getInt(document, "coins");
        this.lastLoaded = getLong(document, "lastLoaded");
        this.lastLogin = getLong(document, "lastLogin");
        this.xp = getLong(document, "xp");
        this.firstJoin = getLong(document, "firstJoin");
        this.lastSave = getLong(document, "lastSave");
        this.nick = document.getString("nick");
        this.lastKnownName = document.getString("lastKnownName");
        this.nickPrefix = document.getString("nickPrefix");
        this.nickColor = document.getString("nickColor");
        this.server = document.getString("server");
        this.authSecret = document.getString("authSecret");
        this.lastSeenServer = document.getString("lastSeenServer");
        this.rankName = document.getString("rankName");
        this.lastSeen = getLong(document, "lastSeen");
        this.lastAuthedIp = document.getString("lastAuthedIp");
        this.lastSeenIp = document.getString("lastSeenIp");
        this.metaDataList = gson.fromJson(document.getString("metaDataList"), GsonType.STRING_LIST);
        this.metaDataList.removeIf(Objects::isNull);
        this.metaData = gson.fromJson(document.getString("metaData"), GsonType.STRING_STRING_MAP);
        this.worldTime = WorldTime.valueOf(document.getString("worldTime"));
        if (document.containsKey("nickTagID")) this.nickTagID = UUID.fromString(document.getString("nickTagID"));
        if (document.containsKey("nickUUID")) this.nickUUID = UUID.fromString(document.getString("nickUUID"));
        if (document.containsKey("tagID")) this.tagID = UUID.fromString(document.getString("tagID"));
        this.playTime = getInt(document, "playTime");
        this.allowedTagsID = gson.fromJson(document.getString("allowedTagsID"), GsonType.UUID_SET);
        if (document.containsKey("nameColor")) this.nameColor = ChatColor.of(document.getString("nameColor"));
        this.nameColorBold = document.getBoolean("nameColorBold");
        this.nameColorItalic = document.getBoolean("nameColorItalic");
        this.staffChatAlerts = document.getBoolean("staffChatAlerts");
        this.adminChatAlerts = document.getBoolean("adminChatAlerts");
        this.reportAlerts = document.getBoolean("reportAlerts");
        this.staffChat = document.getBoolean("staffChat");
        this.adminChat = document.getBoolean("adminChat");
        this.build = document.getBoolean("build");
        this.nodes = gson.fromJson(document.getString("nodes"), GsonType.NODE_LIST);
        this.nodes.removeIf(Objects::isNull);
        this.address = document.getString("address");
        this.socialSpy = document.getBoolean("socialSpy");

        this.messageSettings.setMessagesOff(document.getBoolean("messagesOff"));
        this.messageSettings.getIgnoreList().clear();
        this.messageSettings.setSoundsEnabled(document.getBoolean("sounds"));
        this.messageSettings.setGlobalChat(document.getBoolean("globalChat"));
        this.messageSettings.setChatMention(document.getBoolean("chatMention"));
        this.messageSettings.setIgnoreList(gson.fromJson(document.getString("ignoreList"), GsonType.STRING_LIST));

        this.messageSettings.getIgnoreList().removeIf(u -> u == null || u.isEmpty() || u.equalsIgnoreCase(this.name));

        if (cachedPermissions == null) cachedPermissions = new ConcurrentHashMap<>();
        if (loadNotes == null) loadNotes = new ArrayList<>();

        loaded = true;
    }

    private long getLong(Document doc, String key, long... def) {
        Number number = getNumber(doc, key);
        if (number == null) return def.length > 0 ? def[0] : -1L;
        return number.longValue();
    }

    private int getInt(Document doc, String key, int... def) {
        Number number = getNumber(doc, key);
        if (number == null) return def.length > 0 ? def[0] : -1;
        return number.intValue();
    }

    private double getDouble(Document doc, String key, double... def) {
        Number n = getNumber(doc, key).doubleValue();
        if (n == null) return def.length > 0 ? def[0] : -1;
        return n.doubleValue();
    }

    private Number getNumber(Document doc, String key) {
        Object obj = doc.get(key);
        if (obj != null && obj instanceof Number) return (Number) obj;
        else return null;
    }

    public void save() {
        this.save(false);
    }

    public Document save(boolean getDoc) {
        this.lastDataSave = 0;

        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", name);
        document.put("lowerName", lowerName);
        document.put("grants", OctoCore.getGson().toJson(grants));
        document.put("dataVersion", dataVersion);
        document.put("frozen", frozen);
        document.put("nicked", nicked);
        document.put("authEnabled", authEnabled);
        document.put("vanished", vanished);
        document.put("joinVanished", joinVanished);
        document.put("customColorEnabled", customColorEnabled);
        document.put("customColor", customColor);
        document.put("coins", coins);
        document.put("lastLoaded", lastLoaded);
        document.put("lastLogin", lastLogin);
        document.put("xp", xp);
        document.put("firstJoin", firstJoin);
        document.put("lastSave", lastSave);
        document.put("nick", nick);
        document.put("lastKnownName", lastKnownName);
        document.put("nickPrefix", nickPrefix);
        document.put("nickColor", nickColor);
        document.put("server", server);
        document.put("authSecret", authSecret);
        document.put("lastSeenServer", lastSeenServer);
        document.put("rankName", rankName);
        document.put("lastSeen", lastSeen);
        document.put("lastAuthedIp", lastAuthedIp);
        document.put("lastSeenIp", lastSeenIp);
        document.put("metaDataList", OctoCore.getGson().toJson(metaDataList));
        document.put("metaData", OctoCore.getGson().toJson(metaData));
        document.put("worldTime", worldTime.name());
        if (nickTagID != null) document.put("nickTagID", nickTagID.toString());
        if (nickUUID != null) document.put("nickUUID", nickUUID.toString());
        if (tagID != null) document.put("tagID", tagID.toString());
        document.put("playTime", playTime);
        document.put("allowedTagsID", OctoCore.getGson().toJson(allowedTagsID));
        if (nameColor != null) document.put("nameColor", nameColor.name().toUpperCase());
        document.put("nameColorBold", nameColorBold);
        document.put("nameColorItalic", nameColorItalic);
        document.put("staffChatAlerts", staffChatAlerts);
        document.put("adminChatAlerts", adminChatAlerts);
        document.put("reportAlerts", reportAlerts);
        document.put("staffChat", staffChat);
        document.put("adminChat", adminChat);
        document.put("build", build);
        document.put("nodes", OctoCore.getGson().toJson(nodes));
        document.put("address", address);
        document.put("addresses", StringUtils.getStringFromList(this.addresses));
        document.put("socialSpy", socialSpy);

        document.put("ignoreList", OctoCore.getGson().toJson(this.messageSettings.getIgnoreList(), GsonType.STRING_LIST));

        document.put("globalChat", messageSettings.isGlobalChat());
        document.put("chatMention", messageSettings.isChatMention());
        document.put("sounds", messageSettings.isSoundsEnabled());
        document.put("messagesOff", messageSettings.isMessagesOff());

        document.entrySet().removeIf(e -> e.getValue() == null);
        if (getDoc) {
            document.put("bungeePermissions", OctoCore.getGson().toJson(this.bungeePerms, GsonType.NODE_LIST));

            return document;
        }
        if (PlayerManager.getInstance().doesDocumentExistByUUID(uuid))
            PlayerManager.getInstance().getPdataCollection().replaceOne(Filters.eq("uuid", uuid.toString()), document);
        else PlayerManager.getInstance().getPdataCollection().insertOne(document);
        new CachedData(this.uuid).update(document);
        return document;
    }

    public void onJoin(Player player) {
        //this.lastSeen = DATE_FORMAT.format(new Date());
        this.lastSeen = System.currentTimeMillis();
        name = player.getName();
        lowerName = name.toLowerCase();
        lastKnownName = name;
        this.address = player.getAddress().getAddress().getHostAddress();

        if (hasPermission(Permission.SEND_JOIN_MESSAGE.getNode()) && joinAlert)
            new StaffConnectPacket(getFormattedName(false, player, false), OctoCore.getServerName()).send();
    }

    public void postPermissionLoad(Player player) {
        if (socialSpy && !hasPermission(Permission.SOCIAL_SPY.getNode())) {
            socialSpy = false;
        }
    }

    public void loadAlts(UUID uuid) {
        Document document = PlayerManager.getInstance().getPdataCollection().find(Filters.eq("uuid", uuid.toString())).first();
        if (document == null) {
            return;
        }
        this.loadAlts(document.getString("address"));
    }

    public Alt getAlt(UUID uuid) {
        return this.getAltsSafely().stream().filter(alt -> alt.getUniqueId() == uuid).findFirst().orElse(null);
    }

    public List<Alt> getAltsSafely() {
        List<Alt> alts = new ArrayList<>();
        Iterator<Alt> iterator = this.alts.iterator();

        if (iterator.hasNext()) {
            do {
                alts.add(iterator.next());
            } while (iterator.hasNext());
        }

        return alts;
    }

    public void loadAlts(String address) {
        this.alts.clear();

        try (MongoCursor<Document> cursor = PlayerManager.getInstance().getPdataCollection().find(Filters.eq("address", address)).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                PlayerData playerData = new PlayerData(UUID.fromString(document.getString("uuid")), document.getString("name"));

                playerData.getPunishData().forceLoadActiveBansAndBlacklists();

                if (!playerData.getUuid().toString().equals(this.uuid.toString()) && this.getAlt(playerData.getUuid()) == null) {
                    this.alts.add(new Alt(playerData.getUuid(), playerData.getName(), playerData.getPunishData()).updateDisplayName());
                }
            }
        }

        ServerManager.getInstance().getGlobalPlayers().values().forEach(globalPlayer -> {
            if (!globalPlayer.getUniqueId().toString().equals(this.uuid.toString()) && globalPlayer.getAddress().equalsIgnoreCase(address) && this.getAlt(globalPlayer.getUniqueId()) == null) {
                new AltUpdatePacket(this.uuid, this.name, globalPlayer.getUniqueId(), globalPlayer.getName());
            }
        });

        this.alts.removeIf(alt -> alt.getName().equalsIgnoreCase(this.name));

        List<Alt> alts = new ArrayList<>();
        this.alts.forEach(alt -> { //remove duplicates
            if (alts.stream().filter(current -> current.getName().equalsIgnoreCase(alt.getName())).findFirst().orElse(null) == null) {
                alts.add(alt);
            }
        });

        this.alts.clear();
        this.alts.addAll(alts);
    }


    public String requestName() {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public Node getNode(String perm) {
        return nodes.stream().filter(node -> node.getPermission().equalsIgnoreCase(perm)).findFirst().orElse(null);
    }

    public boolean nodeExists(String perm) {
        return getNode(perm) != null;
    }

    private transient String cachedFormattedNameNoNickNoTag = null;

    public String getCachedFormattedNameNoNickNoTag() {
        if (cachedFormattedNameNoNickNoTag == null) {
            cachedFormattedNameNoNickNoTag = getFormattedName(false, Bukkit.getPlayer(uuid), false);
        }
        return cachedFormattedNameNoNickNoTag;
    }

    public String getFormattedName(boolean nicked, Player player, boolean... showtag) {
        String prefix = getHighestRank().getPrefix();
        boolean shouldShowTag = showtag.length == 0 || showtag[0];
        if (nicked)
            return CC.translate(prefix + (CC.strip(prefix).equals("") ? player.getDisplayName() : " " + player.getDisplayName())) + (getTag() != null && shouldShowTag ? " " + getTagString() : "");
        return CC.translate(prefix +
                getCurrentColor() +
                (CC.strip(prefix).equals("") ?
                        player.getName() : " " +
                        player.getName())) +
                (getTag() != null &&
                        shouldShowTag ? " " +
                        getTagString() : "");
        /*
        if (nicked)
            return CC.translate((this.isNicked() ? nickPrefix : getHighestRank().getPrefix()) + (this.isNicked() ? nickColor : getCurrentColor()) + " " + (this.isNicked() ? nick : lastKnownName)) + (tag != null ? " " + getTagString() : "");
        return CC.translate(getHighestRank().getPrefix() + getNameColor() + " " + lastKnownName) + (tag != null ? " " + getTagString() : "");
         */
    }


    public PlayerData addLoadNote(LoadNote note) {
        this.loadNotes.add(note);
        return this;
    }

    public String getCurrentPrefix() {
        return (isNicked() ? nickPrefix : getHighestRank().getPrefix());
    }

    public String getCurrentColor() {
        return (isNicked() ? nickColor : getActualMainColor());
    }

    public String getActualMainColor() {
        return (customColor != null && isCustomColorEnabled() ? customColor : getHighestRank().getColor().toString());
    }

    public String getPrefixColorOrNull() {
        return (customColor != null && isCustomColorEnabled() ? customColor : null);
    }

    public boolean isOnline(String name) { // FIXME inverted this because its returning false even if they are online
        if (Bukkit.getPlayer(uuid) != null) return true;
        return OctoCore.getInstance().getServerManager().getConnectedServers().stream().filter(serverData -> serverData.getNames().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(name.toLowerCase())).findFirst().orElse(null) != null;
    }

    public boolean isOnlineThisServer() {
        return Bukkit.getPlayer(uuid) != null;
    }

    public boolean isOnline() {
        return isOnline(name);
    }

    public String getTagString() {
        if (getTag() == null) return "";
        if (isNicked()) {
            return CC.D_GRAY + CC.ARROW_LEFT + getNickTag().getTag() + CC.D_GRAY + CC.ARROW_RIGHT;
        }
        return CC.D_GRAY + CC.ARROW_LEFT + getTag().getTag() + CC.D_GRAY + CC.ARROW_RIGHT;
    }

    public boolean hasTag(String tagName) {
        boolean a = false;
        for (PlayerTag playerTag : allowedTags) {
            if (playerTag.getName().equalsIgnoreCase(tagName)) {
                a = true;
                break;
            }
        }
        return a;
    }

    public UUID getMainSkinUUID() {
        return (isNicked() ? nickUUID : uuid);
    }

    public boolean isCustomColorEnabled() {
        if (customColorEnabled) {
            return this.hasPermission(Permission.CUSTOM_COLOR.toString());
        }
        return false;
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
        return this.grants.stream().filter(grant -> !grant.hasExpired() && RankManager.getInstance().getRankById(grant.getRankId()) != null).collect(Collectors.toList());
    }

    public boolean hasRank(Rank rankData) {
        for (Grant grant : this.getActiveGrants()) {
            if (grant.getRankName().equalsIgnoreCase(rankData.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNon() {
        return this.getHighestRank().isDefaultRank(); //F
    }

    public Rank getHighestRank() {
        Grant grant = getHighestGrant();
        if (grant == null) {
            return RankManager.getInstance().getDefaultRank();
        }
        Rank r = grant.getRank();
        if (r == null) {
            return RankManager.getInstance().getDefaultRank();
        }
        return r;
        //return this.getActiveGrants().stream().map(Grant::getRank).max(Comparator.comparingInt(Rank::getWeight)).orElse(RankManager.getInstance().getDefaultRank());
    }

    public Grant getHighestGrant() {
        return this.getActiveGrants().stream().filter(grant -> grant.getRank() != null && grant.getRank().getRankType() != RankType.HIDDEN).max(Comparator.comparingInt(grant -> grant.getRank().getWeight())).orElse(null);
    }

    public Set<Node> getFinalNodes() {
        Set<Node> nodes1 = new HashSet<>(nodes);
        for (Node finalNode : getHighestRank().getFinalNodes()) {
            if (nodes1.stream().filter(node -> node.getPermission().equals(finalNode.getPermission())).findFirst().orElse(null) == null) { //node is not manually set
                nodes1.add(finalNode);
            }
        }
        return nodes1;
    }

    public boolean hasPermission(Node node) {
        return hasPermission(node.getPermission());
    }

    public boolean hasPermission(String perm) { //haha this is a laggy mess
        if (op) return true;
        PermissionResult cachedResult = cachedPermissions.get(perm);
        if (cachedResult != null) {
            if (cachedResult.getTimestamp() + 600000 < System.currentTimeMillis()) { //10 minutes ttl
                cachedPermissions.remove(perm);
            } else return cachedResult.allowed();
        }
        PermissionResult result = PermissionCalculator.hasPermissionResult(perm, getFinalNodes());
        cachedPermissions.put(perm, result);
        if (result.getReason() == PermissionReason.NOT_SET) return getHighestRank().hasPermission(perm);
        else return result.allowed();
    }

    public PermissionResult getPermissionResult(String permission, String server) {
        return PermissionCalculator.hasPermissionResult(permission, getFinalNodes(), server);
    }

    public PermissionResult getPermissionResult(String permission) {
        return PermissionCalculator.hasPermissionResult(permission, getFinalNodes());
    }

    public void applyGrant(Grant grant) {
        grants.add(grant);
        if (Bukkit.getPlayer(uuid) != null) loadPerms(Bukkit.getPlayer(uuid));
        save();
        this.cachedFormattedNameNoNickNoTag = null;
    }

    public void loadPerms(Player player) {
        //Map<String,Boolean> bungeePermissions = new HashMap<>();
        this.cachedFormattedNameNoNickNoTag = null;
        Set<Node> bungeePermissions = new HashSet<>();
        List<Grant> currentGrants = new ArrayList<>(this.grants);
        for (Grant grant : currentGrants) {
            if (grant.hasExpired()) continue;
            Rank rankData = grant.getRank();
            if (rankData != null) {
                bungeePermissions.addAll(rankData.getEffectiveBungeePermissions());
                ArrayList<UUID> inheritances = Lists.newArrayList(rankData.getInheritedRanks());
                inheritances.forEach(inheritance -> {
                    Rank rankInheritance = RankManager.getInstance().getRankById(inheritance);
                    bungeePermissions.addAll(rankInheritance.getEffectiveBungeePermissions());
                });
            }
        }

        Rank defaultRank = RankManager.getInstance().getDefaultRank();
        if (defaultRank != null) {
            bungeePermissions.addAll(defaultRank.getEffectiveBungeePermissions());
            Set<UUID> inheritances = defaultRank.getInheritedRanks();
            inheritances.forEach(inheritance -> {
                Rank rankInheritance = RankManager.getInstance().getRankById(inheritance);
                if (rankInheritance != null) {
                    bungeePermissions.addAll(rankInheritance.getEffectiveBungeePermissions());
                }
            });
        }
        if (!player.getDisplayName().equals(this.getDisplayName())) //TODO handle nicks
            player.setDisplayName(this.getDisplayName());
        this.bungeePerms.clear();
        this.bungeePerms.addAll(bungeePermissions);

        RankManager.getInstance().resetBungeePerms(player);
        postPermissionLoad(player);
    }

    public String getPrefix() {
        return CC.translate(getHighestRank().getPrefix(this.getPrefixColorOrNull()));
    }

    public Map<String, ServerContext> getAllEffectivePermissions() {
        Map<String, ServerContext> permissions = new HashMap<>();
        List<Grant> currentGrants = new ArrayList<>(this.grants);
        for (Grant grant : currentGrants) {
            if (grant.hasExpired()) continue;
            Rank rankData = grant.getRank();
            if (rankData != null) {
                permissions.putAll(rankData.getAllEffectivePermissions());

                List<UUID> inheritances = new ArrayList<>(rankData.getInheritedRanks());
                inheritances.forEach(inheritance -> {
                    Rank rankInheritance = RankManager.getInstance().getRankById(inheritance);

                    if (rankInheritance != null) {
                        permissions.putAll(rankInheritance.getAllEffectivePermissions());
                    }
                });
            }
        }
        Rank defaultRank = RankManager.getInstance().getDefaultRank();
        if (defaultRank != null) {
            permissions.putAll(defaultRank.getAllEffectivePermissions());
            defaultRank.getInheritedRanks().forEach(i -> {
                Rank inherited = RankManager.getInstance().getRankById(i);
                if (inherited != null) permissions.putAll(inherited.getAllEffectivePermissions());
            });
        }
        permissions.putAll(this.getAllSetEffectivePermissions());
        return permissions;
    }

    public String getDisplayName() {
        if (nicked) return CC.translate(getCurrentColor() + getNickColor() + CC.R);
        else return CC.translate(getCurrentColor() + getName() + CC.R);
    }

    public Map<String, ServerContext> getAllNegatedPermissions() {
        Map<String, ServerContext> permissions = new HashMap<>();
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
                    Rank rankInheritance = RankManager.getInstance().getRankById(inheritance);
                    if (rankInheritance != null) permissions.putAll(rankInheritance.getNegatedPermissions());
                });
            }
        }
        Rank defaultRank = RankManager.getInstance().getDefaultRank();
        if (defaultRank != null) {
            permissions.putAll(defaultRank.getNegatedPermissions());
            defaultRank.getInheritedRanks().forEach(i -> {
                Rank inherited = RankManager.getInstance().getRankById(i);
                if (inherited != null) permissions.putAll(inherited.getNegatedPermissions());
            });
        }
        permissions.putAll(getAllSetNegatedPermissions());
        return permissions;
    }

    public Map<String, ServerContext> getAllSetEffectivePermissions() {
        Map<String, ServerContext> a = new HashMap<>();
        nodes.forEach(node -> {
            if (hasPermission(node.getPermission())) a.put(node.getPermission(), node.getServer());
        });
        return a;
    }

    public Map<String, ServerContext> getAllSetNegatedPermissions() {
        Map<String, ServerContext> a = new HashMap<>();
        nodes.forEach(node -> {
            if (node.isNegated()) a.put(node.getPermission(), node.getServer());
        });
        return a;
    }

    public void clearPermCache() {
        cachedPermissions.clear();
        if (Bukkit.getPlayer(uuid) != null) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF(PluginMsgChannels.SubChannels.PERMISSIONS);
                out.writeUTF(PermUpdateType.CLEAR_CACHE.name());
                out.writeUTF(getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.getPlayer(uuid).sendPluginMessage(OctoCore.getInstance(), PluginMsgChannels.PLUGIN_MSG, b.toByteArray());
        }
    }

    public String getLastSeenAgo() {
        if (Bukkit.getPlayer(this.uuid) != null) return "Now";

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.setTime(new Date(lastLogin));
        to.setTime(new Date(System.currentTimeMillis()));

        return DateUtils.formatDateDiff(from, to) + " ago";
    }

    public List<Punishment> getPunishmentsExecuted() {
        if (punishmentsExecuted == null) punishmentsExecuted = new ArrayList<>();
        return punishmentsExecuted;
    }

    public void loadPunishmentsPerformed() {
        this.punishmentsExecuted.clear();

        try (MongoCursor<Document> cursor = PunishModule.getPunishments().find(Filters.eq("addedBy", uuid.toString())).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                punishmentsExecuted.add(new Punishment(document));
            }
        }
    }


    private MongoCollection<Document> getCollection(PunishmentType punishmentType) {
        return PunishModule.getPunishments();
    }

    public Set<PlayerTag> getAllowedTags() {
        if (allowedTags == null) {
            allowedTags = new HashSet<>();
            if (allowedTagsID == null) {
                allowedTagsID = new HashSet<>();
                return allowedTags;
            }
            for (UUID id : allowedTagsID) {
                PlayerTag tag = TagManager.getTag(id);
                if (tag != null) allowedTags.add(tag);
            }
        }
        return allowedTags;
    }

    public boolean hasTag(PlayerTag tag) {
        return hasTag(tag.getId());
    }

    public boolean hasTag(UUID tagID) {
        return allowedTagsID.contains(tagID);
    }

    public void addTag(PlayerTag tag) {
        if (allowedTagsID == null) allowedTagsID = new HashSet<>();
        if (allowedTags == null) {
            allowedTags = getAllowedTags();
        }
        allowedTagsID.add(tag.getId());
        allowedTags.add(tag);
    }

    public void removeTag(UUID tagID) {
        allowedTagsID.remove(tagID);
        allowedTags = null; // Reset cache
    }

    public PlayerTag getTag() {
        return TagManager.getTag(tagID);
    }

    public PlayerTag getNickTag() {
        return TagManager.getTag(nickTagID);
    }

    public void setTag(PlayerTag tag) {
        this.tagID = tag.getId();
        this.allowedTags = null;
    }

    @Override
    public String toString() {
        return OctoCore.getGson().toJson(this);
    }

    @Override
    public Collection<Punishment> getPunishments() {
        return punishData.getPunishments();
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public boolean isBanned() {
        return punishData.isBanned();
    }

    @Override
    public boolean isIPBanned() {
        return punishData.isIPBanned();
    }

    @Override
    public boolean isMuted() {
        return punishData.isMuted();
    }

    @Override
    public boolean isIPMuted() {
        return punishData.isIPMuted();
    }

    @Override
    public boolean isBlacklisted() {
        return punishData.isBlacklisted();
    }

    @Override
    public boolean isWarned() {
        return punishData.isWarned();
    }

    public enum SaveState {
        SAVED, SAVING
    }

    public enum LoadNote {

    }
}
