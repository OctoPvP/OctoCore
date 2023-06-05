package net.octopvp.octocore.master.views.pages.impl;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;
import org.opensaml.saml.config.SAMLConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Login")
@Route(value = "login", layout = MainLayout.class)
@PermitAll
public class LoginPage extends Page {
    @Override
    public void init() {
        // get a list of all the saml idps

    }
}
