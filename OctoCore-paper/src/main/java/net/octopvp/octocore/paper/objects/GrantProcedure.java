package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.DateUtils;

import java.util.UUID;

@Getter
@Setter
public class GrantProcedure {
    public GrantProcedure(PlayerData data){
        this.uuid = data.getUuid();
        this.playerName = data.getName();
    }
    private UUID uuid;
    private GrantProcedureState grantProcedureState = GrantProcedureState.START;
    private long enteredDuration;
    private String enteredReason, rankName, server,playerName;
    private boolean permanent = false;

    public String getNiceDuration() {
        if (isPermanent()) return "Permanent";

        return DateUtils.formatTimeMillis(this.enteredDuration);
    }
    public PlayerData getTargetData(){
        return PlayerManager.getData(uuid);
    }
}
