package net.octopvp.octocore.paper.menus.grant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.DateUtils;

import java.util.UUID;

@Getter
@Setter
public class GrantProcedure {
    public GrantProcedure(PlayerData data){
        this.uuid = data.getUuid();
    }
    private UUID uuid;
    private GrantProcedureState grantProcedureState = GrantProcedureState.START;
    private long enteredDuration;
    private String enteredReason, rankName, server;
    private boolean permanent = false;

    public String getNiceDuration() {
        if (isPermanent()) return "Permanent";

        return DateUtils.formatTimeMillis(this.enteredDuration);
    }
    public PlayerData getTargetData(){
        return PlayerManager.getData(uuid);
    }
}
