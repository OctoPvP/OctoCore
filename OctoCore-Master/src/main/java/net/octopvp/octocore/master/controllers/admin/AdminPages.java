package net.octopvp.aetheriacoremaster.controllers.admin;

import net.octopvp.aetheriacoremaster.config.HttpSessionConfig;
import net.octopvp.aetheriacoremaster.models.APIKey;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.repositories.APIRepository;
import net.octopvp.aetheriacoremaster.repositories.UserRepository;
import net.octopvp.aetheriacoremaster.services.impl.UserDetailsImpl;
import net.octopvp.aetheriacoremaster.stereotypes.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('DEV')")
public class AdminPages {
    @Autowired
    private HttpSessionConfig httpSessionConfig;

    @RequestMapping("/sessions")
    public String tokens(Model model) {
        model.addAttribute("sessions", httpSessionConfig.getActiveSessionsWithoutRevoked());
        return "pages/admin/sessions";
    }

    @Autowired
    private APIRepository apiRepository;

    @RequestMapping("/apikeys")
    public String apikeys(Model model) {
        model.addAttribute("apikeys", apiRepository.findAll());
        return "pages/admin/apikeys";
    }
}
