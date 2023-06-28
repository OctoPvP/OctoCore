package net.octopvp.octocore.common.object.permissions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.common.util.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Grant {
    private UUID id = UUID.randomUUID();
    private final String rankName;
    private final UUID rankId;
    private long addedAt, duration, removedAt;
    private String addedBy, reason, removedBy;
    private UUID addedByUUID, removedByUUID;
    private boolean active, permanent;

    private ServerContext server = new ServerContext("Global");

    public Grant(Rank rank) {
        if (rank == null) {
            throw new IllegalArgumentException("Rank cannot be null");
        }
        this.rankName = rank.getName();
        this.rankId = rank.getRankId();
    }

    public boolean hasExpired() {
        if (server.isThisServer()) {
            if (!this.isActive()) {
                return true;
            }
            if (OctoCoreCommon.getInstance().getRankManager().getRankById(rankId) == null) {
                if (OctoCoreCommon.getInstance().getRankManager().isLoadingRanks()) {
                    return false;
                } else {
                    setActive(false);
                    Logger.debug("cant find rank by rankid: " + rankId); //FIXME - remove on expire
                }
                return true;
            }
            if (this.isPermanent() || duration < 0) {
                return false;
            }
            long expire = this.addedAt + this.duration;
            boolean expired = System.currentTimeMillis() > expire;
            if (expired) {
                setActive(false);
            }
            return expired;
        }
        return true;
    }

    public boolean isActiveSomewhere() {
        if (!this.isActive()) return false;
        if (OctoCoreCommon.getInstance().getRankManager().getRankById(rankId) == null) return false;

        if (!this.server.isGlobal()) {
            ServerData serverData = OctoCoreCommon.getInstance().getServerManager().getServerData(this.server.getServer());
            if (serverData != null && !serverData.getServerName().equalsIgnoreCase(OctoCoreCommon.getInstance().getServerName())) {
                if (isPermanent()) return true;

                return System.currentTimeMillis() < this.addedAt + this.duration;
            }
        }
        return false;
    }

    public boolean isManuallyRemoved() {
        return removedAt > 0;
    }

    public String getNiceDuration() {
        if (isPermanent()) return "Permanent";

        return DateUtils.formatTimeMillis(this.duration);
    }

    public String getNiceExpire() {
        if (!isActive()) return "Not Active";
        if (isPermanent()) return "Never";
        if (hasExpired()) return "Expired";

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.setTime(new Date(System.currentTimeMillis()));
        to.setTime(new Date(this.addedAt + this.getDuration()));

        return "in " + DateUtils.formatDateDiff(from, to);
    }

    public String getExpireDate() {
        if (isPermanent()) return "Never";
        return OctoCoreCommon.DATE_FORMAT.format(new Date(this.addedAt + this.duration));
    }

    public Rank getRank() {
        return OctoCoreCommon.getInstance().getRankManager().getRankById(rankId);
    }

    @Override
    public String toString() {
        return "Grant{" +
                "rankName='" + rankName + '\'' +
                ", rankId=" + rankId +
                ", addedAt=" + addedAt +
                ", duration=" + duration +
                ", removedAt=" + removedAt +
                ", addedBy='" + addedBy + '\'' +
                ", reason='" + reason + '\'' +
                ", removedBy='" + removedBy + '\'' +
                ", addedByUUID=" + addedByUUID +
                ", removedByUUID=" + removedByUUID +
                ", active=" + active +
                ", permanent=" + permanent +
                ", server=" + server +
                '}';
    }

    public long getExpireTime() {
        if (isPermanent()) return -1;
        return addedAt + duration;
    }
}
