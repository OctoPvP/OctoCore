package dev.badbird.launchpad.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import dev.badbird.launchpad.services.UserService;
import dev.badbird.launchpad.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@PageTitle("Test")
@Route(value = "test", layout = MainLayout.class)
@PermitAll
public class AnotherTestView extends VerticalLayout {
    @Autowired
    UserService authenticatedUser;
    private Button button;
    public AnotherTestView() {
        H1 h1 = new H1("Test");
        button = new Button("Test");
        button.addClickListener(e -> {
            NotificationUtils.create("Hello, " + authenticatedUser.get().getUsername() + "!", NotificationVariant.LUMO_SUCCESS).open();
            //Notification.show("Hello " + authenticatedUser.get().getUsername());
        });
        add(h1, button);
    }
}
