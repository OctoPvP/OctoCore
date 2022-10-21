package net.octopvp.octocore.master.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "votes")
@Getter
@Setter
public class VoteModel {
    private long timestamp;
    private String username, serviceName;
    private UUID uuid;

    public VoteModel(long timestamp, String username, UUID uuid, String serviceName) {
        this.timestamp = timestamp;
        this.username = username;
        this.uuid = uuid;
        this.serviceName = serviceName;
    }

    public VoteModel(String username, UUID uuid, String serviceName) {
        this(System.currentTimeMillis(), username, uuid, serviceName);
    }
}
