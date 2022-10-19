package net.octopvp.aetheriacoremaster.controllers;

import net.octopvp.aetheriacoremaster.controllers.api.AuthenticatedAPIController;
import net.octopvp.aetheriacoremaster.master.manager.ServerManager;
import net.octopvp.aetheriacoremaster.services.impl.UserDetailsImpl;
import net.octopvp.aetheriacoremaster.stereotypes.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class PagesController {

    // Logger
    private static final Logger LOG = LoggerFactory
            .getLogger(PagesController.class);

    @RequestMapping("/votes/test")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEV')")
    public String votes(Model model) {
        AuthenticatedAPIController.VoteRequest voteRequest = new AuthenticatedAPIController.VoteRequest();
        model.addAttribute("voterequest", voteRequest);
        return "pages/misc/votes/testvote";
    }

    @RequestMapping("/votes")
    public String votes() {
        return "pages/misc/votes/votes";
    }

    @RequestMapping("/home")
    public String landing(@CurrentUser UserDetailsImpl user, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            LOG.debug("Current authentication instance from security context is null");
        else
            LOG.debug("Current authentication instance from security context: "
                    + this.getClass().getSimpleName());
        System.out.println("User: " + user);
        model.addAttribute("servers", ServerManager.getInstance().getConnectedServers().size());
        System.out.println("Roles:");
        for (GrantedAuthority authority : user.getAuthorities()) {
            System.out.println(authority.getAuthority());
        }
        return "pages/home";
    }

    @RequestMapping("/servers")
    public String servers(@CurrentUser UserDetailsImpl user, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            LOG.debug("Current authentication instance from security context is null");
        else
            LOG.debug("Current authentication instance from security context: "
                    + this.getClass().getSimpleName());
        //model.addAttribute("username", 	user.getUsername());

        model.addAttribute("servers", new ArrayList<>(ServerManager.getInstance().getConnectedServers()));

        return "pages/servers";
    }

    @RequestMapping("/redis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEV')")
    public String redis() {
        return "pages/misc/redis";
    }

    @RequestMapping("/")
    public ModelAndView index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("request - " + auth);
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            System.out.println("Current authentication instance from security context is null");
            return new ModelAndView("pages/login/login");
        } else
            LOG.debug("Current authentication instance from security context: "
                    + this.getClass().getSimpleName());
        return new ModelAndView("redirect:/home");
    }

}
