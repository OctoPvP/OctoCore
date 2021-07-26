package net.octopvp.octocore.paper.objects.permissions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Material;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.ServerData;
import net.octopvp.octocore.paper.utils.DateUtils;
import org.bukkit.entity.Player;

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
    private UUID addedByUUID,removedByUUID;
    private boolean active, permanent;
    private ServerContext server = new ServerContext("Global");
    public Grant(Rank rank){
        this.rankName = rank.getName();
        this.rankId = rank.getRankId();
    }
    public boolean hasExpired() {
        if (server.isThisServer()) {
            if (!this.isActive()) return true;
            if (RankManager.getRankById(rankId) == null) {
                Logger.debug("cant find rank by rankid: " + rankId);
                return true;
            }
            if (this.isPermanent()) return false;
            if (duration == -1) return false;
            boolean b = System.currentTimeMillis() >= this.addedAt + this.duration;
            if (!b)
                active = false;
            return b;
        }
        return true;
    }

    public boolean isActiveSomewhere() {
        if (!this.isActive()) return false;
        if (RankManager.getRankById(rankId) == null) return false;

        if (!this.server.isGlobal()) {
            ServerData serverData = OctoCore.getServerManager().getServerData(this.server.getServer());
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
        return RankManager.getRankById(rankId);
    }
}
