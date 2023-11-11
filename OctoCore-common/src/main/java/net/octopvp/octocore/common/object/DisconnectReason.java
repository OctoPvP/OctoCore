package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.util.CC;

@Getter
@Setter
@RequiredArgsConstructor
public class DisconnectReason {
    private final String reason;

    @Override
    public String toString() {
        return CC.AQUA + CC.B + "OctoMC\n\n" + CC.YELLOW + reason; //TODO: Center reason
    }
}
