package net.octopvp.octocore.master.views.pages;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import net.octopvp.octocore.master.master.OctoCoreMaster;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import java.util.TimeZone;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout {

    @Autowired
    private UserService authenticatedUser;

    @Autowired
    private OctoCoreMaster master;

    public HomeView() {
    }
    @PostConstruct
    public void postConstruct() {
        System.out.println("HomeView - " + authenticatedUser  + " | " + master);
        if (authenticatedUser.get() != null)
            loggedIn();
    }
    public void loggedIn() {
        String s = "%greeting% %user%";
        TimeZone timeZone = authenticatedUser.get().getTimeZone();
        // Determine if it is either morning, afternoon, evening, or night
        int time = timeZone.getOffset(System.currentTimeMillis()) / 1000 / 60 / 60;
        String greeting;
        if (time < 12) {
            greeting = "Good morning,";
        } else if (time < 17) {
            greeting = "Good afternoon,";
        } else if (time < 20) {
            greeting = "Good evening,";
        } else {
            greeting = "Good night,";
        }
        H1 h1 = new H1(s.replace("%user%", authenticatedUser.get().getUsername()).replace("%greeting%", greeting));
        Div div = new Div();
        div.add(h1);
        add(div);
    }
}
