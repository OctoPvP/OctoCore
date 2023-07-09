package net.octopvp.octocore.common.object;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.interfaces.IPlayerData;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.mfa.MFAData;
import net.octopvp.octocore.common.mfa.MFAType;
import net.octopvp.octocore.common.object.enums.RankType;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.punish.PunishData;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.ChatColor;
import net.octopvp.octocore.common.util.GsonType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;
import net.octopvp.octocore.common.util.permissions.PermissionReason;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.octopvp.octocore.common.util.DocumentUtils.*;

@Data
public class SimplePlayerData implements IPlayerData, IPunishData {
    protected UUID uuid;
    protected double dataVersion = 0.0;
    protected long lastLoaded, lastLogin, xp = 0, firstJoin = System.currentTimeMillis(), lastSave = System.currentTimeMillis(), lastSeen = -1;
    protected String nick, customColor, lastKnownName = "<unknown>", nickPrefix, nickColor, name = lastKnownName;
    protected String lowerName = name.toLowerCase(), server, authSecret, lastSeenServer = "Unknown", rankName = "default";
    protected String lastAuthedIp = "", lastSeenIp = "", address, lastServerOn = "Unknown";
    protected List<String> metaDataList = new ArrayList<>();
    protected Map<String, String> metaData = new ConcurrentHashMap<>();
    protected UUID tagID = null, nickTagID = null, nickUUID;
    protected int /*playtime in seconds, dont need to make it an long since 2.1b seconds is 66 years*/
            playTime = 0, coins;
    protected HashSet<UUID> allowedTagsID = new HashSet<>();
    protected boolean nameColorBold = false, nameColorItalic = false, staffChatAlerts = true, adminChatAlerts = true;
    protected boolean reportAlerts = true, staffChat = false, adminChat = false, build = false;
    protected boolean frozen, nicked = false, authEnabled = false, vanished = false, joinVanished = false;
    protected boolean customColorEnabled = false, savingOnQuit = false, loaded = false, fullJoined = false;
    protected boolean joinAlert = false, socialSpy = false;
    protected Collection<Alt> alts = new ArrayList<>();
    protected List<String> addresses = new ArrayList<>();
    protected List<UUID> ignoredPlayers = new ArrayList<>();
    protected MessageSettings messageSettings = new MessageSettings();
    protected List<Node> nodes = new ArrayList<>();
    protected ArrayList<Grant> grants = new ArrayList<>();
    protected PunishData punishData = new PunishData(this);
    protected WorldTime worldTime = WorldTime.DAY;
    protected ChatColor nameColor = ChatColor.GREEN;
    protected Map<String, MFAData> mfaData = new HashMap<>();

    public SimplePlayerData(UUID uuid) { // not sure if we need name too, have a look at PlayerData in OctoCore-Core
        this.uuid = uuid;
    }

    public SimplePlayerData loadGrants(Document document) {
        Gson gson = OctoCoreCommon.getInstance().getGson();
        this.grants = gson.fromJson(document.getString("grants"), GsonType.GRANT);
        this.grants.removeIf(Objects::isNull);
        return this;
    }

    public SimplePlayerData load(Document document) {
        Gson gson = OctoCoreCommon.getInstance().getGson();
        this.name = OctoCoreCommon.getInstance().getServerImplementation().getName(uuid);

        this.lowerName = name.toLowerCase();
        this.lastLoaded = System.currentTimeMillis();
        loadGrants(document);
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
        if (document.containsKey("nameColor")) this.nameColor = ChatColor.valueOf(document.getString("nameColor"));
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
        this.messageSettings.setIgnoreList(gson.fromJson(document.getString("ignoreList"), GsonType.STRING_UUID_MAP));

        if (messageSettings.getIgnoreList() == null) {
            messageSettings.setIgnoreList(new HashMap<>());
        }
        this.messageSettings.getIgnoreList().keySet().removeIf(Objects::isNull);

        String mfaDataString = document.getString("mfaData");
        if (mfaDataString != null && !mfaDataString.isEmpty()) {
            JsonObject mfaDataJson = OctoCoreCommon.getInstance().getGson().fromJson(mfaDataString, JsonObject.class);
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : mfaDataJson.entrySet()) {
                String key = stringJsonElementEntry.getKey();
                MFAType type = MFAType.findType(key);
                if (type == null) {
                    Logger.error("Could not find MFAType for key " + key);
                    continue;
                }
                JsonObject value = stringJsonElementEntry.getValue().getAsJsonObject();
                this.mfaData.put(key, MFAData.deserialize(type, value));
            }
        }
        return this;
    }

    public Document getData() {
        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", name);
        document.put("lowerName", lowerName);
        document.put("grants", OctoCoreCommon.getInstance().getGson().toJson(grants.clone()));
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
        document.put("metaDataList", OctoCoreCommon.getInstance().getGson().toJson(metaDataList));
        document.put("metaData", OctoCoreCommon.getInstance().getGson().toJson(metaData));
        document.put("worldTime", worldTime.name());
        if (nickTagID != null) document.put("nickTagID", nickTagID.toString());
        if (nickUUID != null) document.put("nickUUID", nickUUID.toString());
        if (tagID != null) document.put("tagID", tagID.toString());
        document.put("playTime", playTime);
        document.put("allowedTagsID", OctoCoreCommon.getInstance().getGson().toJson(allowedTagsID));
        if (nameColor != null) document.put("nameColor", nameColor.name().toUpperCase());
        document.put("nameColorBold", nameColorBold);
        document.put("nameColorItalic", nameColorItalic);
        document.put("staffChatAlerts", staffChatAlerts);
        document.put("adminChatAlerts", adminChatAlerts);
        document.put("reportAlerts", reportAlerts);
        document.put("staffChat", staffChat);
        document.put("adminChat", adminChat);
        document.put("build", build);
        document.put("nodes", OctoCoreCommon.getInstance().getGson().toJson(nodes));
        document.put("address", address);
        document.put("addresses", StringUtils.getStringFromList(this.addresses));
        document.put("socialSpy", socialSpy);

        document.put("ignoreList", OctoCoreCommon.getInstance().getGson().toJson(this.messageSettings.getIgnoreList(), GsonType.STRING_UUID_MAP));

        document.put("globalChat", messageSettings.isGlobalChat());
        document.put("sounds", messageSettings.isSoundsEnabled());
        document.put("messagesOff", messageSettings.isMessagesOff());

        if (mfaData != null && !mfaData.isEmpty()) {
            JsonObject mfaDataJson = new JsonObject();
            for (Map.Entry<String, MFAData> stringMFADataEntry : mfaData.entrySet()) {
                mfaDataJson.add(stringMFADataEntry.getKey(), stringMFADataEntry.getValue().serializeFully());
            }
            document.put("mfaData", OctoCoreCommon.getInstance().getGson().toJson(mfaDataJson));
        }

        document.entrySet().removeIf(e -> e.getValue() == null);
        return document;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    public String getPrefix() {
        return CC.translate(getHighestRank().getPrefix(this.getPrefixColorOrNull()));
    }

    public String getPrefixColorOrNull() {
        return (customColor != null && isCustomColorEnabled() ? customColor : null);
    }

    public Rank getHighestRank() {
        Grant grant = getHighestGrant();
        if (grant == null) {
            return OctoCoreCommon.getInstance().getRankManager().getDefaultRank();
        }
        Rank r = grant.getRank();
        if (r == null) {
            return OctoCoreCommon.getInstance().getRankManager().getDefaultRank();
        }
        return r;
        //return this.getActiveGrants().stream().map(Grant::getRank).max(Comparator.comparingInt(Rank::getWeight)).orElse(RankManager.getInstance().getDefaultRank());
    }

    public Grant getHighestGrant() {
        return this.getActiveGrants().stream().filter(grant ->
                        grant.getRank() != null && grant.getRank().getRankType() != RankType.HIDDEN)
                .max(Comparator.comparingInt(grant -> grant.getRank().getWeight())).orElse(null);
    }

    public long getLowestGrantExpire() {
        return this.getActiveGrants().stream().mapToLong(Grant::getExpireTime).min().orElse(-1);
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

    public List<Grant> getActiveGrants() {
        return this.grants.stream().filter(grant -> !grant.hasExpired() && OctoCoreCommon.getInstance().getRankManager().getRankById(grant.getRankId()) != null).collect(Collectors.toList());
    }

    public boolean hasRank(Rank rankData) {
        for (Grant grant : this.getActiveGrants()) {
            if (grant.getRankName().equalsIgnoreCase(rankData.getName())) {
                return true;
            }
        }
        return false;
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

    public String getNameColor() {
        if (this.nameColor == null) {
            return this.getHighestRank().getDisplayColor();
        }
        if (this.isNameColorBold() && this.isNameColorItalic()) {
            return this.nameColor + ChatColor.BOLD.toString() + ChatColor.ITALIC;
        }
        if (this.isNameColorBold()) {
            return this.nameColor + ChatColor.BOLD.toString();
        }
        if (this.isNameColorItalic()) {
            return this.nameColor + ChatColor.ITALIC.toString();
        }
        return this.nameColor.toString();
    }

    public boolean isCustomColorEnabled() {
        if (customColorEnabled) {
            return this.hasPermission(Permissions.CUSTOM_COLOR);
        }
        return false;
    }

    public boolean hasPermission(Node node) {
        return hasPermission(node.getPermission());
    }

    public boolean hasPermission(String perm) {
        PermissionResult result = PermissionCalculator.hasPermissionResult(perm, getFinalNodes());
        if (result.getReason() == PermissionReason.NOT_SET)
            return getHighestRank().hasPermission(perm); // Delegate to highest rank
        else return result.allowed();
    }

    public PermissionResult getPermissionResult(String permission, String server) {
        return PermissionCalculator.hasPermissionResult(permission, getFinalNodes(), server);
    }

    public PermissionResult getPermissionResult(String permission) {
        return PermissionCalculator.hasPermissionResult(permission, getFinalNodes());
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

    @Override
    public IPunishment getActiveBan() {
        return punishData.getActiveBan();
    }

    @Override
    public IPunishment getActiveMute() {
        return punishData.getActiveMute();
    }

    @Override
    public IPunishment getActiveBlacklist() {
        return punishData.getActiveBlacklist();
    }

    @Override
    public List<IPunishment> getPunishments(PunishmentType type) {
        return punishData.getPunishments(type);
    }

    @Override
    public Collection<IPunishment> getPunishments() {
        return punishData.getPunishments();
    }

}
