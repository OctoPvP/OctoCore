package net.octopvp.octocore.master.views.pages;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@PageTitle("Servers")
@Route(value = "servers", layout = MainLayout.class)
@PermitAll
public class Servers extends VerticalLayout {
    @Autowired
    UserService authenticatedUser;
    private Button button;

    public Servers() {
        H1 h1 = new H1("Test");
        button = new Button("Test");
        button.addClickListener(e -> {
            NotificationUtils.create("Hello, " + authenticatedUser.get().getUsername() + "!", NotificationVariant.LUMO_SUCCESS).open();
            //Notification.show("Hello " + authenticatedUser.get().getUsername());
        });
        add(h1, button);
    }
}
