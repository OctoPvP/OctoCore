package net.octopvp.octocore.master.views.pages.impl;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;

import javax.annotation.security.RolesAllowed;

@PageTitle("Staff Chat")
@Route(value = "staffchat", layout = MainLayout.class)
@RolesAllowed("USER")
public class StaffChat extends Page {
    @Override
    public void init() {

    }
}
