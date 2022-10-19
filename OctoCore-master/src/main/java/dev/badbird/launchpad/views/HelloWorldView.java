package dev.badbird.launchpad.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import dev.badbird.launchpad.services.UserService;
import dev.badbird.launchpad.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class HelloWorldView extends VerticalLayout {

    @Autowired
    UserService authenticatedUser;

    private Button sayHello;

    public HelloWorldView() {

        /*
         * This trivial Vaadin session serializes just fine. To make testing
         * pros of JWT authentication, make it non-serializable. Object does
         * not serialize because of Java :-). This hack will make the session
         * lost on each server restart.
         */
        VaadinSession.getCurrent().setAttribute("foo", new Object());

        sayHello = new Button("Say hello!");
        sayHello.addClickListener(e -> {
            NotificationUtils.create("Hello, " + authenticatedUser.get().getUsername() + "!", NotificationVariant.LUMO_SUCCESS).open();
            //Notification.show("Hello " + authenticatedUser.get().getUsername());
        });

        add(sayHello);
    }

}
