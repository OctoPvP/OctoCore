package net.octopvp.octocore.paper.objects.permissions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.ServerData;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Grant {
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
            if (!this.isActive()) return true;
            if (RankManager.getInstance().getRankById(rankId) == null) {
                setActive(false);
                Logger.debug("cant find rank by rankid: " + rankId); //FIXME - remove on expire
                return true;
            }
            if (this.isPermanent() || duration < 0) return false;
            boolean b = System.currentTimeMillis() >= this.addedAt + this.duration;
            if (!b)
                active = false;
            return b;
        }
        return true;
    }

    public boolean isActiveSomewhere() {
        if (!this.isActive()) return false;
        if (RankManager.getInstance().getRankById(rankId) == null) return false;

        if (!this.server.isGlobal()) {
            ServerData serverData = OctoCore.getInstance().getServerManager().getServerData(this.server.getServer());
            if (serverData != null && !serverData.getServerName().equalsIgnoreCase(OctoCore.getServerName())) {
                if (isPermanent()) return true;

                return System.currentTimeMillis() < this.addedAt + this.duration;
            }
        }
        return false;
    }

    public String getNiceDuration() {
        if (isPermanent()) return "Permanent";

        return DateUtils.formatTimeMillis(this.duration);
    }

    public String getNiceExpire() {
        if (!isActive()) return "Expired";
        if (isPermanent()) return "Never";
        if (hasExpired()) return "Expired";

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.setTime(new Date(System.currentTimeMillis()));
        to.setTime(new Date(this.addedAt + this.getDuration()));

        return DateUtils.formatDateDiff(from, to);
    }

    public Rank getRank() {
        return RankManager.getInstance().getRankById(rankId);
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
}
