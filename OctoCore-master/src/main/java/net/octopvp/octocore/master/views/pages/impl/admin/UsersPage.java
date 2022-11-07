package net.octopvp.octocore.master.views.pages.impl.admin;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

@PageTitle("Users")
@Route(value = "admin/users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersPage extends Page {
    @Override
    public void init() {

    }
}
