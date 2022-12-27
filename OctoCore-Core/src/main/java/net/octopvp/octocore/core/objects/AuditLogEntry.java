package net.octopvp.octocore.core.objects;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.core.objects.enums.AuditLogType;

import java.util.HashMap;

@Getter
@Setter
public class AuditLogEntry {
    private HashMap<String, String> entries;
    private String type = "Undefined";

    public AuditLogEntry(HashMap<String, String> entries, AuditLogType type) {
        this.entries = entries;
        switch (type) {
            case WORLDEDIT_ACTION:
                this.type = "WorldEdit";
                break;
            case BAN:
                this.type = "Ban";
                break;
            case MUTE:
                this.type = "Mute";
                break;
            case KICK:
                this.type = "Kick";
                break;
            default:
                this.type = "Undefined";
        }
    }

    public AuditLogEntry(String key, String value, AuditLogType type) {
        HashMap<String, String> e = new HashMap<>();
        e.put(key, value);
        this.entries = e;
        switch (type) {
            case WORLDEDIT_ACTION:
                this.type = "WorldEdit";
                break;
            case BAN:
                this.type = "Ban";
                break;
            case AUTH_FAIL:
                this.type = "Auth Fail";
                break;
            case MUTE:
                this.type = "Mute";
                break;
            case KICK:
                this.type = "Kick";
                break;
            default:
                this.type = "Undefined";
        }
        //TODO discord send queue
    }

}
