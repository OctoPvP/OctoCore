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
        return CC.RED + CC.B + "OctoPvP\n\n" + CC.YELLOW + reason;
    }
}
