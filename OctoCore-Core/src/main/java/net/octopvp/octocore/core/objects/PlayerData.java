package net.octopvp.octocore.core.objects;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.WorldTime;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.perms.Node;
import net.octopvp.octocore.common.util.perms.PermissionCheckResult;
import net.octopvp.octocore.common.util.perms.PermissionManager;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.player.AltUpdatePacket;
import net.octopvp.octocore.core.database.redis.packets.staff.StaffConnectPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.TagManager;
import net.octopvp.octocore.core.manager.impl.VanishManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerData extends SimplePlayerData {
    //TODO set defaults for this so theres no errors when using/loading old data from older updates (idk if this makes sense lol)
    //private Map<String, Pair<ServerContext,Boolean>> permissions = new HashMap<>();
    private transient boolean fullyJoined = false, op = false;
    private transient int lastDataSave = 0;
    private transient String lastMessage; //only applies to this server for spam prot (maybe :))
    private transient Set<PlayerTag> allowedTags;
    private transient List<Punishment> punishmentsExecuted = new ArrayList<>();
    private transient List<LoadNote> loadNotes = new ArrayList<>();
    private transient GrantProcedure grantProcedure = null;
    private transient Map<String, PermissionCheckResult> cachedPermissions = new ConcurrentHashMap<>();

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
        updateName();
        if (document == null) {
            document = PlayerManager.getInstance().getProfileDocument(uuid);
        }
        boolean newData = document == null;
        if (newData) {
            save();
        } else {
            super.load(document);
            if (cachedPermissions == null) cachedPermissions = new ConcurrentHashMap<>();
            if (loadNotes == null) loadNotes = new ArrayList<>();
        }
        loaded = true;
        return this;
    }

    public SimplePlayerData loadGrants() {
        return super.loadGrants(PlayerManager.getInstance().getProfileDocument(uuid));
    }

    public Document save() {
        this.lastDataSave = 0;
        Document document = super.getData();
        PlayerManager.getInstance().getPdataCollection().replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
        Document copy = new Document(document);
        copy.put("calculated-nodes", OctoCoreCommon.getInstance().getGson().toJson(getFinalNodes()));
        new DataCache(this.uuid).update(copy);
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
            new StaffConnectPacket(getFormattedName(false, player, false), OctoCore.getServerName(), VanishManager.getInstance().getVanishPriority(this), joinVanished).send();

        updateTime(player);
    }

    public void postPermissionLoad(Player player) {
        if (socialSpy && !hasPermission(Permissions.SOCIAL_SPY)) {
            socialSpy = false;
        }
        player.setPlayerListName(getDisplayName());
        OctoCore.getInstance().getServerImplementation().updatePlayerCommands(player);
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
        Logger.debug("Loading alts for " + this.name + " (" + this.uuid + ") IP: " + address);
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

        OctoCoreCommon.getInstance().getServerManager().getOnlinePlayers().forEach(player -> {
            if (!player.getUuid().equals(this.uuid) && player.getAddress().equalsIgnoreCase(address) && this.getAlt(player.getUuid()) == null) {
                new AltUpdatePacket(this.uuid, this.name, player.getUuid(), player.getName());
            }
        });

        List<Alt> nAlts = new ArrayList<>(this.alts);
        this.alts.clear();
        this.alts.addAll(Alt.removeDuplicates(nAlts, this));
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
        return PermissionManager.getInstance().findNode(perm, nodes);
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
        String displayName = player != null ? player.getDisplayName() : getDisplayName();
        boolean shouldShowTag = showtag.length == 0 || showtag[0];
        if (nicked)
            return CC.translate(prefix + (CC.strip(prefix).isEmpty() ? displayName : " " + displayName)) + (getTag() != null && shouldShowTag ? " " + getTagString() : "");
        return CC.translate(prefix + getCurrentColor() + (CC.strip(prefix).equals("") ? name : " " + name)) + (getTag() != null && shouldShowTag ? " " + getTagString() : "");
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
        return (customColor != null && isCustomColorEnabled() ? customColor : getHighestRank().getColor().toString());
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
            return CC.GRAY + CC.ARROW_LEFT + getNickTag().getTag() + CC.GRAY + CC.ARROW_RIGHT;
        }
        return CC.GRAY + CC.ARROW_LEFT + getTag().getTag() + CC.GRAY + CC.ARROW_RIGHT;
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

    public PermissionCheckResult calculatePermissionResult(String perm) {
        PermissionCheckResult cachedResult = cachedPermissions.get(perm);
        if (cachedResult != null) {
            // if (cachedResult.getTimestamp() + 600000 < System.currentTimeMillis()) { // 10 minutes ttl
            long expire = cachedResult.getExpire();
            if (expire != -1 && expire < System.currentTimeMillis()) {
                cachedPermissions.remove(perm);
            } else return cachedResult;
        }
        PermissionCheckResult result = PermissionManager.getInstance().checkPermission(perm, getFinalNodes(), OctoCoreCommon.getInstance().getServerName());
        long nextGrantExpire = this.getLowestGrantExpire();
        if (nextGrantExpire != -1 || System.currentTimeMillis() - nextGrantExpire > 1200000)
            result.setExpire(System.currentTimeMillis() + 600000); // expire in 10 minutes
            // expire this permission when one of the grants expire
        else result.setExpire(nextGrantExpire);

        cachedPermissions.put(perm, result);
        if (result.getReason() == PermissionCheckResult.Reason.NOT_SET) return getHighestRank().calculatePermission(perm);
        return result;
    }

    @Override
    public boolean hasPermission(String perm) { // haha this is a laggy mess | update: 9/7/2023 - rewrote to a faster tree system
        PermissionCheckResult result = calculatePermissionResult(perm);
        if (result.getReason() == PermissionCheckResult.Reason.NOT_SET) return op;
        else return result.allowed();
    }

    public void applyGrant(Grant grant) {
        grants.add(grant);
        if (Bukkit.getPlayer(uuid) != null) loadPerms(Bukkit.getPlayer(uuid));
        save();
        this.cachedFormattedNameNoNickNoTag = null;
    }

    public void loadPerms(Player player) {
        this.cachedFormattedNameNoNickNoTag = null;
        if (!player.getDisplayName().equals(this.getDisplayName())) //TODO handle nicks
            player.setDisplayName(this.getDisplayName());

        postPermissionLoad(player);
    }

    public String getDisplayName() {
        if (nicked) return CC.translate(getCurrentColor() + getNick() + CC.R);
        else return CC.translate(getCurrentColor() + getName() + CC.R);
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

    public boolean isVanished() {
        return VanishManager.getInstance().isVanished(uuid);
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    public int getVanishPriority() {
        return VanishManager.getInstance().getVanishPriority(this);
    }

    public enum SaveState {
        SAVED, SAVING
    }

    public enum LoadNote {

    }
}
