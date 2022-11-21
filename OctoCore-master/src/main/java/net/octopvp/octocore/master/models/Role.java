package net.octopvp.octocore.master.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "roles")
@Getter
@Setter
public class Role {
    @Id
    private String name;

    private int priority;

    @DBRef
    private Set<Role> children = new HashSet<>();

    public Role(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void addChildren(Role... role) {
        Collections.addAll(children, role);
    }
}
