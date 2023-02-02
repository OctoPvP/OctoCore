package net.octopvp.octocore.master.views.pages.impl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.AllArgsConstructor;
import net.octopvp.octocore.master.models.Setting;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.components.ThemeToggleButton;
import net.octopvp.octocore.master.views.pages.Page;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
@RolesAllowed("USER")
public class SettingsPage extends Page {

    @Autowired
    private UserService authenticatedUser;
    @Autowired
    private MongoUserRepository mongoUserRepository;
    private User user;

    @Override
    public void init() {
        user = authenticatedUser.get();
        /*
        VerticalLayout vl = new VerticalLayout();
        vl.add(new Hr(), new Span("Apperance"), toggleButton, new Hr(), new Span("Account"), new Button("dummy"), new Hr());
        add(vl);
        */
        add(new AppearanceSection());
    }
    public static abstract class SettingsSection extends VerticalLayout {
        public SettingsSection() {
            add(new Span(getName()));
            getComponents().forEach(this::add);
            add(new Hr());
        }
        public abstract List<Component> getComponents();
        public abstract String getName();
    }
    public class AppearanceSection extends SettingsSection {
        @Override
        public List<Component> getComponents() {
            Select<String> timeZoneSelect = new Select<>();
            timeZoneSelect.setLabel("Timezone");
            timeZoneSelect.setItems(TimeZone.getAvailableIDs());
            timeZoneSelect.setValue(user.getTimezoneId());
            timeZoneSelect.addValueChangeListener(event -> {
                System.out.println("Timezone changed to " + event.getValue());
                user.setTimezoneId(event.getValue());
                mongoUserRepository.save(user);
            });

            return Arrays.asList(new ThemeToggleButton(mongoUserRepository, authenticatedUser, user.getUserID()), timeZoneSelect);
        }

        @Override
        public String getName() {
            return "Appearance";
        }
    }
    public class SecuritySection extends SettingsSection {
        @Override
        public List<Component> getComponents() {
            return Arrays.asList(new Button("Dummy"));
        }

        @Override
        public String getName() {
            return "Appearance";
        }
    }
}
