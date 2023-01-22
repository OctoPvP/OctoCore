package net.octopvp.octocore.master.views.pages.impl.player;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.*;
import lombok.extern.java.Log;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishData;
import net.octopvp.octocore.master.master.manager.PlayerManager;
import net.octopvp.octocore.master.master.util.AccountUtil;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.components.PlayerName;
import net.octopvp.octocore.master.views.pages.Page;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.*;

@PageTitle("Player Info")
@Route(value = "player/view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Log
public class PlayerInfoPage extends Page implements HasUrlParameter<String> {
    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private PlayerManager playerManager;
    @Autowired
    private UserService userService;
    private User user;
    private UUID uuid;
    private String name;
    private Location location;

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        user = userService.get();
        uuid = UUID.fromString(parameter);
        name = accountUtil.getName(uuid);
        location = event.getLocation();
        populate(location);
    }

    public void populate(Location location) {
        HorizontalLayout title = new HorizontalLayout();
        title.add(new PlayerName(name, true));
        Button refresh = new Button(VaadinIcon.REFRESH.create());
        refresh.addClickListener(clickEvent -> {
            removeAll();
            populate(location);
        });
        refresh.getStyle().set("float", "right");
        title.add(refresh);
        if (!playerManager.doesDocumentExistByUUID(uuid)) {
            add(title, new Span("This player has never joined the network."));
            return;
        }
        SimplePlayerData playerData = playerManager.getData(uuid);

        TabSheet tabSheet = new TabSheet();
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabSheet.getStyle().set("width", "100%");

        tabSheet.add("Overview", createOverview(playerData));
        tabSheet.add("Punishments", createPunishments(playerData));
        tabSheet.add("Notes", new Div(new Text(("Data here"))));
        tabSheet.add("Reports", new Div(new Text(("Data here"))));


        tabs:
        {
            Map<String, List<String>> param = location.getQueryParameters().getParameters();
            if (param.containsKey("tab") && param.get("tab").size() > 0) {
                String tab = param.get("tab").get(0);
                try {
                    int tabIndex = Integer.parseInt(tab) - 1;
                    tabSheet.setSelectedIndex(tabIndex);
                } catch (NumberFormatException ignored) {
                    // ignored
                }
            }
        }
        tabSheet.addSelectedChangeListener(event -> {
            // set the query parameter
            Map<String, List<String>> currentParameters = new HashMap<>(location.getQueryParameters().getParameters());
            currentParameters.put("tab", List.of(String.valueOf(tabSheet.getIndexOf(event.getSelectedTab()) + 1)));
            String query = new QueryParameters(currentParameters).getQueryString();
            UI.getCurrent().getPage().executeJs("window.history.replaceState({}, '', $0)", location.getPath() + (query.isEmpty() ? "" : "?" + query));
        });

        add(title, tabSheet);
    }

    @Override
    public void init() {

    }

    public Component createOverview(SimplePlayerData playerData) {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Span("UUID: " + playerData.getUuid()));
        layout.add(new Span("First Join: " + user.formatDate(playerData.getFirstJoin()))); // TODO: format & timezone
        layout.add(new Span("Last Seen: " + user.formatDate(playerData.getLastSeen()))); // TODO: format & timezone
        return layout;
    }

    public Component createPunishments(SimplePlayerData playerData) {
        VerticalLayout layout = new VerticalLayout();
        PunishData data = playerData.getPunishData();
        data.load();

        Grid<IPunishment> grid = new Grid<>(IPunishment.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        GridListDataView<IPunishment> dataView = grid.setItems(data.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).toList());

        grid.addColumn(punish -> StringUtils.capatalizeFirstDeep(punish.getType().name())).setHeader("Type");
        grid.addColumn(IPunishment::getReason).setHeader("Reason");
        grid.addColumn(nameRenderer()).setHeader("Issuer");
        grid.addColumn(punish -> user.formatDate(punish.getAddedAt())).setHeader("Issued");
        grid.addColumn(punish -> {
            long removeTimestamp = punish.getRemoveTimestamp();
            if (removeTimestamp < 0) {
                return "Never";
            }
            return user.formatDate(removeTimestamp);
        }).setHeader("Expires");
        grid.addColumn(createStatusComponentRenderer()).setHeader("Status");

        layout.add(grid);

        return layout;
    }

    private static final SerializableBiConsumer<Span, IPunishment> statusComponentUpdater = (
            span, punishment) -> {
        boolean isActive = punishment.isActive();
        String theme = String.format("badge %s",
                !isActive ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(isActive ? "Active" : (punishment.getRemovedBy() != null && !punishment.getRemovedBy().isEmpty() ? "Removed" : "Expired"));
    };

    private static ComponentRenderer<Span, IPunishment> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
    }

    private ComponentRenderer<Component, IPunishment> nameRenderer() {
        return new ComponentRenderer<>(punishment -> {
            HorizontalLayout layout = new HorizontalLayout();
            PlayerName pName = new PlayerName(punishment.getAddedByName(), false, true);
            layout.add(pName);
            return layout;
        });
    }
}
