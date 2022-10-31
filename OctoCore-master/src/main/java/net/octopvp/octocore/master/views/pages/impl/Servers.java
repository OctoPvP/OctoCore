package net.octopvp.octocore.master.views.pages.impl;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
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
    private FeederThread thread;

    @Autowired
    UserService authenticatedUser;

    private Grid<ServerData> grid;
    private GridListDataView<ServerData> dataView;

    private TextField searchField = new TextField();

    @Override
    public void init() { // A table of servers
        grid = new Grid<>(ServerData.class, false);
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

        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());
        update();
        add(searchField, grid);
    }

    public void update() {
        dataView = grid.setItems(ServerManager.getInstance().getDummyServerData());

        dataView.addFilter(serverData -> {
            String searchTerm = searchField.getValue().trim().toLowerCase();

            if (searchTerm.isEmpty())
                return true;

            return serverData.getServerName().toLowerCase().contains(searchTerm);
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        thread = new FeederThread(attachEvent.getUI(), this);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        thread.interrupt();
        thread = null;
    }

    private static class FeederThread extends Thread {
        private final UI ui;
        private final Servers view;

        public FeederThread(UI ui, Servers view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                ui.access(view::update);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
