package net.octopvp.octocore.core.objects;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.object.*;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.common.util.GsonType;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;
import net.octopvp.octocore.common.util.permissions.PermissionReason;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.player.AltUpdatePacket;
import net.octopvp.octocore.core.database.redis.packets.staff.StaffConnectPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.manager.impl.TagManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerData extends SimplePlayerData {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    //TODO set defaults for this so theres no errors when using/loading old data from older updates (idk if this makes sense lol)
    //private Map<String, Pair<ServerContext,Boolean>> permissions = new HashMap<>();
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


    private transient String cachedFormattedNameNoNickNoTag = null;

    public PlayerData(UUID uuid, String name) {
        super(uuid);
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

    public SimplePlayerData load(Document document) {
        if (document == null) {
            document = PlayerManager.getInstance().getProfileDocument(uuid);
        }
        if (document == null) {
            return null;
        }
        super.load(document);
        if (cachedPermissions == null) cachedPermissions = new ConcurrentHashMap<>();
        if (loadNotes == null) loadNotes = new ArrayList<>();
        loaded = true;
        return this;
    }

    public Document save() {
        return save(true);
    }

    public Document save(boolean getDoc) {
        this.lastDataSave = 0;
        Document document = super.getData(getDoc);
        if (getDoc) {
            document.put("bungeePermissions", OctoCoreCommon.getInstance().getGson().toJson(this.bungeePerms, GsonType.NODE_LIST));

            return document;
        }
        if (PlayerManager.getInstance().doesDocumentExistByUUID(uuid))
            PlayerManager.getInstance().getPdataCollection().replaceOne(Filters.eq("uuid", uuid.toString()), document);
        else PlayerManager.getInstance().getPdataCollection().insertOne(document);
        new DataCache(this.uuid).update(document);
        return document;
    }

    public void onJoin(Player player) {
        //this.lastSeen = DATE_FORMAT.format(new Date());
        this.lastSeen = System.currentTimeMillis();
        name = player.getName();
        lowerName = name.toLowerCase();
        lastKnownName = name;
        this.address = player.getAddress().getAddress().getHostAddress();

        if (hasPermission(Permissions.SEND_JOIN_MESSAGE) && joinAlert)
            new StaffConnectPacket(getFormattedName(false, player, false), OctoCore.getServerName()).send();

        updateTime(player);
    }

    public void postPermissionLoad(Player player) {
        if (socialSpy && !hasPermission(Permissions.SOCIAL_SPY)) {
            socialSpy = false;
        }
        player.setPlayerListName(getDisplayName());
    }

    public void loadAlts(UUID uuid) {
        Document document = PlayerManager.getInstance().getPdataCollection().find(Filters.eq("uuid", uuid.toString())).first();
        if (document == null) {
            return;
        }
        this.loadAlts(document.getString("address"));
    }

    public Alt getAlt(UUID uuid) {
        if (uuid == null) return null;
        return this.getAltsSafely().stream().filter(alt -> alt.getUniqueId() == uuid).findFirst().orElse(null);
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

        OctoCoreCommon.getInstance().getServerManager().getGlobalPlayers().forEach(globalPlayer -> {
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

    public void updateTime(Player player) {
        switch (worldTime) {
            case DEFAULT:
                player.resetPlayerTime();
                return;
            case SUNRISE:
                player.setPlayerTime(WorldTime.SUNRISE.getTime(), false);
                break;
            case DAY:
                player.setPlayerTime(WorldTime.DAY.getTime(), false);
                break;
            case SUNSET:
                player.setPlayerTime(WorldTime.SUNSET.getTime(), false);
                break;
            case NIGHT:
                player.setPlayerTime(WorldTime.NIGHT.getTime(), false);
                break;
        }
    }

    public String requestName() {
        // return Bukkit.getOfflinePlayer(uuid).getName();
        return OfflineHelpers.getOfflineInfo(uuid).getName();
    }

    public Node getNode(String perm) {
        return nodes.stream().filter(node -> node.getPermission().equalsIgnoreCase(perm)).findFirst().orElse(null);
    }

    public boolean nodeExists(String perm) {
        return getNode(perm) != null;
    }

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

    public String getCurrentPrefix() {
        return (isNicked() ? nickPrefix : getHighestRank().getPrefix());
    }

    public String getCurrentColor() {
        return (isNicked() ? nickColor : getActualMainColor());
    }

    public String getActualMainColor() {
        return (customColor != null && isCustomColorEnabled() ?
                customColor :
                getHighestRank()
                        .getColor()
                        .toString());
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

    @Override
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

    public void applyGrant(Grant grant) {
        grants.add(grant);
        if (Bukkit.getPlayer(uuid) != null) loadPerms(Bukkit.getPlayer(uuid));
        getData();
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

    public void setTag(PlayerTag tag) {
        this.tagID = tag != null ? tag.getId() : null;
        this.allowedTags = null;
    }

    public PlayerTag getNickTag() {
        return TagManager.getTag(nickTagID);
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    public enum SaveState {
        SAVED, SAVING
    }

    public enum LoadNote {

    }
}
