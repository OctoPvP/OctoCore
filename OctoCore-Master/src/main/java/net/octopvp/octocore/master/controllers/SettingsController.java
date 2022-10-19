package net.octopvp.aetheriacoremaster.controllers;

import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.repositories.UserRepository;
import net.octopvp.aetheriacoremaster.services.impl.UserDetailsImpl;
import net.octopvp.aetheriacoremaster.stereotypes.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/settings")
public class SettingsController {
    private static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);

    @RequestMapping("/user")
    public ModelAndView userSettings(@CurrentUser UserDetailsImpl user, Model model) {
        LOG.debug("user page");
        return new ModelAndView("pages/settings/user");
    }

    @RequestMapping
    public String settings(@CurrentUser UserDetailsImpl user) {
        LOG.debug("settings page");
        return "Hello World!";
    }
}
