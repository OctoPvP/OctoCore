package net.octopvp.octocore.master.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@Getter
@Setter
public class Role {
    @Id
    private String name;

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
