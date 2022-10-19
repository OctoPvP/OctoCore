package net.octopvp.octocore.master.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Document(collection = "users")
@Getter
@Setter
public class User {
    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    private Set<String> roles = new HashSet<>();

    @Id
    private String userID = UUID.randomUUID().toString();

    private Set<String> ownedResources = ConcurrentHashMap.newKeySet();

    private String profilePictureURL = "https://cdn.carbonhost.cloud/6201479d7b237373ab269385/assets/profile.png";

    private boolean darkMode = true;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userID, user.userID);
    }

    public Set<String> getOwnedResourceIds() {
        return ownedResources;
    }

    public Set<String> getOwnedResourceIdsClone() {
        Set<String> set = ConcurrentHashMap.newKeySet();
        set.addAll(ownedResources);
        return set;
    }
}
