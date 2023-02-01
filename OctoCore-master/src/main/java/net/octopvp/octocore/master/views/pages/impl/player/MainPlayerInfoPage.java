package net.octopvp.octocore.master.views.pages.impl.player;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.common.util.Utilities;
import net.octopvp.octocore.master.master.util.AccountUtil;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;
import net.octopvp.octocore.master.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;

@PageTitle("Player Info")
@Route(value = "player/info", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class MainPlayerInfoPage extends Page {
    @Autowired
    private AccountUtil accountUtil;

    TextField nameField;

    @Override
    public void init() {
        nameField = new TextField();
        nameField.setWidthFull();
        nameField.setLabel("Name/UUID");
        nameField.setRequired(true);
        nameField.addClassNames("centered");
        nameField.addKeyUpListener(event -> {
            if (event.getKey() == Key.ENTER || event.getKey() == Key.NUMPAD_ENTER) {
                submit();
            }
        });
        Button button = new Button("Submit");
        button.addClassNames("centered");
        button.getElement().setAttribute("type", "submit");
        button.addClickListener(event -> {
            submit();
        });
        add(nameField, button);
    }

    public void submit() {
        String name = nameField.getValue();
        boolean isUUID = Utilities.isUUID(name);
        if (!isUUID) {
            Notification notification = NotificationUtils.create("Searching for player, this may take a second...", NotificationVariant.LUMO_PRIMARY);
            notification.setDuration(10 * 1000);
            notification.open();
        }
        UUID uuid = isUUID ? UUID.fromString(name) : accountUtil.getUUID(name);
        //redirect to /player/info/<uuid>
        if (uuid != null) {
            getUI().ifPresent(ui -> ui.navigate("/player/view/" + uuid));
        } else {
            Notification notification = NotificationUtils.create("Player not found!", NotificationVariant.LUMO_ERROR);
            notification.setDuration(10 * 1000);
            notification.open();
        }
    }
}
