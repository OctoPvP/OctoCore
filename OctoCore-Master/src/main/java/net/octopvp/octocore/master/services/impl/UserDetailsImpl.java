package net.octopvp.aetheriacoremaster.services.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.aetheriacoremaster.models.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String email;

    private Collection<? extends GrantedAuthority> authorities;

    private String profilePicture = UserModel.DEFAULT_PROFILE_PICTURE;

    public UserDetailsImpl(String id, String username, String email, String profilePicture,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
        this.profilePicture = profilePicture;
    }

    public static UserDetailsImpl build(UserModel user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    String authority = role.toUpperCase();
                    if (!authority.startsWith("ROLE_")) {
                        authority = "ROLE_" + authority;
                    }
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfilePicture(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "Hello World!"; // Password is handled by SAML 2.0
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}

