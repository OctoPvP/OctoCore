package net.octopvp.octocore.master.views.pages.impl.misc;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;
import net.octopvp.octocore.master.views.util.NotificationUtils;

import jakarta.annotation.security.PermitAll;

@PageTitle("Redis Manager")
@Route(value = "redismgr", layout = MainLayout.class)
@PermitAll
public class RedisManagerPage extends Page {
    @Override
    public void init() {
        TextArea jsonField = new TextArea();
        jsonField.setWidthFull();
        jsonField.setLabel("JSON");
        jsonField.setRequired(true);
        jsonField.addClassNames("centered");
        Button button = new Button("Submit");
        button.addClassNames("centered");
        button.getElement().setAttribute("type", "submit");
        button.addClickListener(event -> {
            String json = jsonField.getValue();
            if (json.isEmpty()) {
                return;
            }
            RedisManager redisManager = OctoCoreCommon.getInstance().getRedisManager();
            redisManager.write(json);
            NotificationUtils.create("JSON published to Redis.", NotificationVariant.LUMO_SUCCESS).open();
        });
        add(jsonField, button);
    }
}
