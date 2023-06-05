package net.octopvp.octocore.master.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.octopvp.octocore.master.models.Role;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.saml2.Saml2LoginConfigurer;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class Saml2LoginSettings implements Customizer<Saml2LoginConfigurer<HttpSecurity>> {
    private static final Map<String, String> roleMap = new HashMap<>();
    static {
        roleMap.put("Managers", "ROLE_MANAGER");
        roleMap.put("Admins", "ROLE_ADMIN");
        roleMap.put("Users", "ROLE_USER");
        roleMap.put("Developers", "ROLE_DEV");
        roleMap.put("Moderators", "ROLE_MOD");
    }
    @Autowired
    private MongoUserRepository mongoUserRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void customize(Saml2LoginConfigurer<HttpSecurity> t) {
        t.successHandler(new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                authentication = assignAuthorities(authentication, request);
                super.onAuthenticationSuccess(request, response, authentication);
            }
        });
    }

    private Authentication assignAuthorities(Authentication authentication, HttpServletRequest request) {
        Collection<SimpleGrantedAuthority> oldAuthorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        DefaultSaml2AuthenticatedPrincipal princ = (DefaultSaml2AuthenticatedPrincipal) authentication.getPrincipal();
        /*
        if (princ.getAttribute("urn:oid:1.3.6.1.4.1.5923.1.1.1.7").contains("urn:mace:dir:entitlement:common-lib-terms")) {
            List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
            updatedAuthorities.addAll(oldAuthorities);
            updatedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
            Saml2Authentication sAuth = (Saml2Authentication) authentication;

            sAuth = new Saml2Authentication((AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), sAuth.getSaml2Response(), updatedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(sAuth);

            return sAuth;
        } else return authentication;
         */
        // username attribute: http://schemas.goauthentik.io/2021/02/saml/username
        // email attribute: http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress
        List<String> usernameList = princ.getAttribute("http://schemas.goauthentik.io/2021/02/saml/username");
        List<String> emailList = princ.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress");
        List<String> rolesList = princ.getAttribute("http://schemas.xmlsoap.org/claims/Group");
        List<Role> roles = new ArrayList<>(roleRepository.findAll());
        List<Object> idpId = princ.getAttribute("http://schemas.goauthentik.io/2021/02/saml/uid");
        List<GrantedAuthority> authorities = new ArrayList<>();
        Set<Role> rolesSet = new HashSet<>();
        for (String role : rolesList) {
            String roleName = roleMap.getOrDefault(role, role);
            Optional<Role> roleOptional = roles.stream().filter(r -> r.getName().equalsIgnoreCase(roleName)).findFirst();
            if (roleOptional.isPresent()) {
                Role r = roleOptional.get();
                rolesSet.add(r);
                if (!authorities.contains(new SimpleGrantedAuthority(r.getName()))) {
                    authorities.add(new SimpleGrantedAuthority(r.getName()));
                }
            }
        }
        String email = emailList != null && emailList.size() > 0 ? emailList.get(0) : null;
        String username = usernameList != null && usernameList.size() > 0 ? usernameList.get(0) : email.split("@")[0];
        Optional<User> user = mongoUserRepository.findByUsername(username);
        if (user.isEmpty()) {
            user = mongoUserRepository.findByEmail(email);
            if (user.isEmpty()) {
                User newUser = new User();
                newUser.setUsername(username);
                if (email != null) {
                    newUser.setEmail(email);
                }
                if (idpId != null && idpId.size() > 0) {
                    newUser.setIdpID(idpId.get(0).toString());
                }
                newUser.setRoles(rolesSet);
                mongoUserRepository.save(newUser);
            }
        } else {
            // check if we need to update anything like roles
            User u = user.get();
            boolean update = false;
            if (!u.getRoles().equals(rolesSet)) {
                u.setRoles(rolesSet);
                update = true;
            }
            if (idpId != null && idpId.size() > 0) {
                String id = idpId.get(0).toString();
                if (!id.equals(u.getIdpID())) {
                    u.setIdpID(id);
                    update = true;
                }
            }
            if (update) mongoUserRepository.save(u);
        }
        Saml2Authentication sAuth = (Saml2Authentication) authentication;
        sAuth = new Saml2Authentication((AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), sAuth.getSaml2Response(), authorities);
        SecurityContextHolder.getContext().setAuthentication(sAuth);
        return authentication;
    }
}
