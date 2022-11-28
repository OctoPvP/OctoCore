package net.octopvp.octocore.core.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BlackListedCommand {
    private String cmd, servers, disabledBy;
    private long disabledOn, disabledUntil;

    public BlackListedCommand(String cmd) {
        this.cmd = cmd;
        this.disabledOn = System.currentTimeMillis();
        this.servers = "GLOBAL";
        this.disabledBy = "00000000-0000-0000-0000-000000000000";
    }
}
