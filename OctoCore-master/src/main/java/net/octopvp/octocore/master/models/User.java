package net.octopvp.octocore.master.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;
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

    @Email
    private String email;

    @DBRef
    private Set<Role> roles = new HashSet<>();

    @Id
    private String userID = UUID.randomUUID().toString();

    private String timezoneId = "America/Toronto";

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

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(timezoneId);
    }

    public int getHighestRolePriority() {
        int highestPriority = 0;
        for (Role role : roles) {
            if (role.getPriority() > highestPriority) {
                highestPriority = role.getPriority();
            }
        }
        return highestPriority;
    }

    public Set<Role> getActualRoles() {
        return roles;
    }

    public Set<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
        for (Role role : getActualRoles()) {
            roles.add(role);
            roles.addAll(role.getChildren());
        }
        return roles;
    }
}
