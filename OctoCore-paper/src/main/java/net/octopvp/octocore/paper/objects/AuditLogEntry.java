package net.octopvp.octocore.paper.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class AuditLogEntry {
    private HashMap<String,String> entries;
    private String type;
    public AuditLogEntry(HashMap<String,String> entries,AuditLogType type ){
        this.entries = entries;
        switch (type){
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

}
