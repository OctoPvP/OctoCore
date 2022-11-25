package net.octopvp.octocore.master.views.pages.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import net.octopvp.octocore.master.master.OctoCoreMaster;
import net.octopvp.octocore.master.master.manager.ServerManager;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import java.util.TimeZone;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends Page {

    @Autowired
    private UserService authenticatedUser;

    @Autowired
    private OctoCoreMaster master;

    public HomeView() {
    }

    @Override
    public void init() {
        if (authenticatedUser != null && authenticatedUser.get() != null)
            loggedIn();
    }

    public void loggedIn() {
        String s = "%greeting% %user%";
        TimeZone timeZone = authenticatedUser.get().getTimeZone();
        // Determine if it is either morning, afternoon, evening, or night
        int time = timeZone.getOffset(System.currentTimeMillis()) / 1000 / 60 / 60;
        String greeting = "Hello, ";
        /*
        if (time >= 0 && time < 12) {
            greeting = "Good morning";
        } else if (time >= 12 && time < 17) {
            greeting = "Good afternoon";
        } else if (time >= 17 && time < 20) {
            greeting = "Good evening";
        } else {
            greeting = "Good night";
        }
         */
        H1 h1 = new H1(s.replace("%user%", authenticatedUser.get().getUsername()).replace("%greeting%", greeting));
        Text text = new Text("Servers: " + ServerManager.getInstance().getConnectedServers().size());
        Component statusText = null;
        switch (master.getStatus()) {
            case OK: {
                statusText = new H3("OK");
                statusText.getElement().setAttribute("style", "color: green");
                break;
            }
            case DEGRADED: {
                statusText = new H3("Degraded");
                statusText.getElement().setAttribute("style", "color: orange");
                break;
            }
            case DOWN: {
                statusText = new H3("Down");
                statusText.getElement().setAttribute("style", "color: red");
                break;
            }
        }
        H3 h3 = new H3(new H3("Status: "), statusText);
        Div div = new Div();
        div.add(h1, text, h3);
        // center the div
        div.getStyle().set("margin", "auto");
        div.getStyle().set("text-align", "center");
        add(div);
    }
}
