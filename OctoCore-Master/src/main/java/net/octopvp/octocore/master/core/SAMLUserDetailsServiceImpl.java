package net.octopvp.aetheriacoremaster.core;

import lombok.SneakyThrows;
import net.octopvp.aetheriacoremaster.config.HttpSessionConfig;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.repositories.UserRepository;
import net.octopvp.aetheriacoremaster.services.impl.UserDetailsImpl;
import org.opensaml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class SAMLUserDetailsServiceImpl implements SAMLUserDetailsService {

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(SAMLUserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Value("${saml.attribute.email}")
    private String emailAttribute;
    @Value("${saml.attribute.username}")
    private String usernameAttribute;
    @Value("${saml.attribute.fullname}")
    private String fullnameAttribute;
    @Value("${saml.attribute.groups}")
    private String groupsAttribute;

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired(required = false)
    private HttpSessionConfig sessionConfig;

    @SneakyThrows
    public Object loadUserBySAML(SAMLCredential credential)
            throws UsernameNotFoundException {

        // The method is supposed to identify local account of user referenced by
        // data in the SAML assertion and return UserDetails object describing the user.

        String userID = credential.getAttributeAsString(fullnameAttribute);
        //credential.getNameID().getValue();
        LOG.info(userID + " is logged in");
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(authority);
        String[] groups = credential.getAttributeAsStringArray(groupsAttribute);
        String userName = credential.getAttributeAsString(usernameAttribute);
        LOG.info("User name: " + userName);

        for (Attribute attribute : credential.getAttributes()) {
            LOG.info("Attribute: " + attribute.getName());
        }

        for (String group : groups) {
            LOG.info("User is in group " + group);
            if (group.startsWith("Master-")) {
                String newGroup = group.replace("Master-", "").toUpperCase();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + newGroup));
            }
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            LOG.info("User has authority " + grantedAuthority.getAuthority());
        }

        Set<String> roles = new HashSet<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            roles.add(grantedAuthority.getAuthority());
        }

        // In a real scenario, this implementation has to locate user in a arbitrary
        // dataStore based on information present in the SAMLCredential and
        // returns such a date in a form of application specific UserDetails object.
        Optional<UserModel> user;
        if (userRepository.existsByEmail(credential.getAttributeAsString(emailAttribute))) {
            user = userRepository.findByEmail(credential.getAttributeAsString(emailAttribute));
        } else if (userRepository.existsByUsername(credential.getAttributeAsString(usernameAttribute))) {
            user = userRepository.findByUsername(credential.getAttributeAsString(usernameAttribute));
        } else {
            user = Optional.empty();
        }
        UserDetailsImpl userDetails;
        if (user.isPresent()) {
            UserModel u = user.get();
            u.setRoles(roles);
            u.onLogin();
            userDetails = UserDetailsImpl.build(u);
            userRepository.save(u);
        } else {
            String username = credential.getAttributeAsString(usernameAttribute);
            String email = credential.getAttributeAsString(emailAttribute);
            UserModel newUser = new UserModel(username, email);
            newUser.setRoles(roles);
            newUser.onLogin();
            userRepository.save(newUser);
            userDetails = UserDetailsImpl.build(newUser);
        }

        LOG.info("User " + userDetails.getUsername() + " is now logged in.");

        if (request != null && sessionConfig != null) {
            LOG.info("Request: " + request.getRequestURL().toString());
            sessionConfig.updateUser(request.getSession(true), userDetails.getId(), userDetails.getUsername(), request);
        }

        return userDetails;
        //return new User(userID, "<abc123>", true, true, true, true, authorities);
    }

}
