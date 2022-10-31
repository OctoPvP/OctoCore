package net.octopvp.octocore.master.views.pages.impl;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.master.master.manager.ServerManager;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;
import net.octopvp.octocore.master.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;

@PageTitle("Servers")
@Route(value = "servers", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class Servers extends Page {
    @Autowired
    UserService authenticatedUser;

    @Override
    public void init() { // A table of servers
        Grid<ServerData> grid = new Grid<>(ServerData.class, false);
        //grid.addColumn(createServerRenderer()).setHeader("Name").setFlexGrow(0)
        //        .setWidth("230px");
        // Name | Players | Whitelist | Maintenance | TPS | Actions
        grid.addColumn(ServerData::getServerName).setHeader("Name");
        grid.addColumn(serverData -> serverData.getOnlinePlayers().size() + "/" + serverData.getMaxPlayers()).setHeader("Players");
        grid.addColumn(ServerData::isWhitelisted).setHeader("Whitelisted");
        grid.addColumn(ServerData::isMaintenance).setHeader("Maintenance");
        grid.addColumn(ServerData::getFormattedTPS).setHeader("TPS");
        grid.addComponentColumn((ValueProvider<ServerData, Component>) serverData -> {
            Button restart = new Button("Restart");
            restart.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> NotificationUtils.create("Restart command queued.", NotificationVariant.LUMO_SUCCESS).open());
            return restart;
        }).setHeader("Actions");
        GridListDataView<ServerData> dataView = grid.setItems(ServerManager.getInstance().getDummyServerData());

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(serverData -> {
            String searchTerm = searchField.getValue().trim().toLowerCase();

            if (searchTerm.isEmpty())
                return true;

            return serverData.getServerName().toLowerCase().contains(searchTerm);
        });
        add(searchField, grid);
        UI.getCurrent().setPollInterval(1500);
    }

    private static Renderer<ServerData> createServerRenderer() {
        // For TPS use getFormattedTPS()
        return LitRenderer.of(
                "<div class='server'>"
                        + "<div class='name'>[[item.serverName]]</div>"
                        + "<div class='players'>[[item.onlinePlayers.length]]/[[item.maxPlayers]]</div>"
                        + "<div class='whitelist'>[[item.whitelisted]]</div>"
                        + "<div class='maintenance'>[[item.maintenance]]</div>"
                        + "<div class='tps'>[[item.getFormattedTPS()]]</div>"
                        + "<div class='actions'>"
                        + "<vaadin-button theme='primary' on-click='[[item.serverName]]'>Connect</vaadin-button>"
                        + "</div>"
                        + "</div>"
        );
    }
}
