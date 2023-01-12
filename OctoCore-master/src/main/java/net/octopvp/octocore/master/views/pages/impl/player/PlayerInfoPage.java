package net.octopvp.octocore.master.views.pages.impl.player;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.master.master.manager.ServerManager;
import net.octopvp.octocore.master.master.util.AccountUtil;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.components.PlayerName;
import net.octopvp.octocore.master.views.pages.Page;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.awt.*;
import java.util.UUID;

@PageTitle("Player Info")
@Route(value = "player/view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class PlayerInfoPage extends Page implements HasUrlParameter<String> {
    @Autowired
    private AccountUtil accountUtil;
    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        UUID uuid = UUID.fromString(parameter);
        String name = accountUtil.getName(uuid);
        HorizontalLayout title = new HorizontalLayout();
        title.add(new PlayerName(name, true));
        add(title);
    }

    @Override
    public void init() {

    }
}
