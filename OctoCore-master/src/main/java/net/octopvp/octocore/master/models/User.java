package net.octopvp.octocore.master.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Document(collection = "users")
@Getter
@Setter
public class User {
    @NotBlank
    @Size(max = 20)
    private String username;

    @Email
    private String email;

    @DBRef
    private Set<Role> roles = new HashSet<>();

    @Id
    private String userID = UUID.randomUUID().toString();

    private String idpID;

    private String timezoneId = "America/Toronto";

    private String profilePictureURL = "https://cdn.carbonhost.cloud/6201479d7b237373ab269385/assets/profile.png";

    private boolean darkMode = true;
    private UUID minecraftUUID;
    private String minecraftName = getUsername();

    public User() {
    }

    public User(String username) {
        this.username = username;
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

    // 2022-11-10 3:11 PM
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");

    public String formatDate(long timestamp) {
        return formatDate(new Date(timestamp));
    }

    public String formatDate(Date date) {
        dateFormat.setTimeZone(getTimeZone());
        return dateFormat.format(date);
    }

    public String formatDate(LocalDate date) {
        return formatDate(date.atStartOfDay(getTimeZone().toZoneId()).toInstant().toEpochMilli());
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

    public String getMinecraftName() {
        if (minecraftName == null) {
            return minecraftName = getUsername();
        }
        return minecraftName;
    }
}
