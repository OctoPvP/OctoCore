package net.octopvp.octocore.master.views.pages.impl.admin;

import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;

import java.util.UUID;

@PageTitle("Users")
@Route(value = "user", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ViewUserPage extends Page implements HasUrlParameter<String> {
    private UUID uuid;
    private Location location;

    @Override
    public void init() {

    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        uuid = UUID.fromString(parameter);
        location = event.getLocation();
        populate(location);
    }

    public void populate(Location location) {

    }
}
