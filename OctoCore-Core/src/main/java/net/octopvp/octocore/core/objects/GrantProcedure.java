package net.octopvp.octocore.core.objects;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.manager.impl.PlayerManager;

import java.util.UUID;

@Getter
@Setter
public class GrantProcedure {
    private UUID uuid;
    private GrantProcedureState grantProcedureState = GrantProcedureState.START;
    private long enteredDuration;
    private String enteredReason, rankName, server, playerName;
    private boolean permanent = false;

    public GrantProcedure(PlayerData data) {
        this.uuid = data.getUuid();
        this.playerName = data.getName();
    }

    public String getNiceDuration() {
        if (isPermanent()) return "Permanent";

        return DateUtils.formatTimeMillis(this.enteredDuration);
    }
}
