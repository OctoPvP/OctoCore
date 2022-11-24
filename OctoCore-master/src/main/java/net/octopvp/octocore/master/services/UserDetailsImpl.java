package net.octopvp.octocore.master.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.octopvp.octocore.master.models.User;
import org.springframework.security.saml.saml2.attribute.Attribute;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private final String username;

    @JsonIgnore
    private final String password;

    private String userID;

    private final Collection<? extends GrantedAuthority> authorities;
    public UserDetailsImpl(String username, String password,
                           Collection<? extends GrantedAuthority> authorities, String userID) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.userID = userID;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    if (!role.getName().toUpperCase().startsWith("ROLE_")) return new SimpleGrantedAuthority("ROLE_" + role);
                    return new SimpleGrantedAuthority(role.getName());
                })
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getUsername(),
                user.getPassword(),
                authorities,
                user.getUserID()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(userID, that.userID) && Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, userID, authorities);
    }

    public String getUserID() {
        return userID;
    }
    public List<Attribute> getSamlAttributesToSendToSP() {
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute().setName("userID").setValues(Collections.singletonList(userID)));
        list.add(new Attribute().setName("username").setValues(Collections.singletonList(username)));
        list.add(new Attribute().setName("roles").setValues(Arrays.asList(authorities.stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))));
        return list;
    }
}
