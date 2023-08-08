package net.octopvp.octocore.master.services;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserService {

    private static final String LOGOUT_SUCCESS_URL = "/";

    private Optional<Authentication> getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(context.getAuthentication())
                .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken));
    }

    @Autowired
    private MongoUserRepository userRepository;

    public User get() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            System.out.println("Auth is null");
            return null;
        }
        Saml2Authentication saml2Authentication = (Saml2Authentication) authentication;
        String name = saml2Authentication.getName();
        System.out.println("Auth name: " + name);
        if (name.contains("@"))
            return userRepository.findByEmail(authentication.getName()).orElse(null);
        else
            return userRepository.findByUsername(authentication.getName()).orElse(null);
    }

    public void logout() {
        //UI.getCurrent().getPage().setLocation(WebSecurityConfig.LOGOUT_URL); // FIXME
        UI.getCurrent().getPage().setLocation("/");
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

}
