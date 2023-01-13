package net.octopvp.octocore.master.views.pages.impl.player;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.punish.BasePunishment;
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
import java.util.UUID;

@PageTitle("Player Info")
@Route(value = "player/view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class PlayerInfoPage extends Page implements HasUrlParameter<String> {
    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private PlayerManager playerManager;
    @Autowired
    private UserService userService;
    User user;

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        user = userService.get();
        UUID uuid = UUID.fromString(parameter);
        String name = accountUtil.getName(uuid);
        HorizontalLayout title = new HorizontalLayout();
        title.add(new PlayerName(name, true));
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
        GridListDataView<IPunishment> dataView = grid.setItems(data.getPunishments());

        grid.addColumn(punish -> StringUtils.capatalizeFirstDeep(punish.getType().name())).setHeader("Type");
        grid.addColumn(IPunishment::getReason).setHeader("Reason");
        grid.addColumn(IPunishment::getAddedByName).setHeader("Issuer");
        grid.addColumn(punish -> user.formatDate(punish.getAddedAt())).setHeader("Issued");
        grid.addColumn(punish -> user.formatDate(punish.getWhenRemoved())).setHeader("Expires");
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
}
