package net.octopvp.octocore.master.views.pages.impl.player;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.*;
import lombok.Getter;
import lombok.extern.java.Log;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishData;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.Utilities;
import net.octopvp.octocore.master.master.manager.PlayerManager;
import net.octopvp.octocore.master.master.util.AccountUtil;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.util.TabUtils;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.components.PlayerName;
import net.octopvp.octocore.master.views.pages.Page;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

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

        PunishData punishData = playerData.getPunishData();
        punishData.load();

        tabSheet.add("Overview", createOverview(playerData, punishData));
        tabSheet.add("Punishments", createPunishments(playerData, punishData));
        tabSheet.add("Notes", new Div(new Text(("Data here"))));
        tabSheet.add("Reports", new Div(new Text(("Data here"))));

        TabUtils.persistSelection(tabSheet, location);

        add(title, tabSheet);
    }

    @Override
    public void init() {

    }

    public Component createOverview(SimplePlayerData playerData, PunishData punishData) {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Span("UUID: " + playerData.getUuid()));
        layout.add(new Span("First Join: " + user.formatDate(playerData.getFirstJoin())));
        layout.add(new Span("Last Seen: " + user.formatDate(playerData.getLastSeen())));
        return layout;
    }

    public Component createPunishments(SimplePlayerData playerData, PunishData data) {
        VerticalLayout layout = new VerticalLayout();

        Grid<IPunishment> grid = new Grid<>(IPunishment.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        GridListDataView<IPunishment> dataView = grid.setItems(data.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).toList());
        PunishmentFilter filter = new PunishmentFilter(dataView);

        grid.addColumn(punish -> StringUtils.capatalizeFirstDeep(punish.getType().name())).setHeader(createDropdownFilter("Type", Utilities.addAllNewList(PunishmentType.getNames(), "All"), filter::setType, (select)-> {
            select.addComponents(PunishmentType.getNames().get(PunishmentType.getNames().size() - 1), new Hr());
        }));
        grid.addColumn(IPunishment::getReason).setHeader(createFilterType("Reason", filter::setReason));
        grid.addColumn(nameRenderer()).setHeader(createFilterType("Punisher", filter::setIssuer));
        grid.addColumn(punish -> user.formatDate(punish.getAddedAt())).setHeader("Issued");
        grid.addColumn(punish -> {
            long removeTimestamp = punish.getRemoveTimestamp();
            if (removeTimestamp < 0) {
                return "Never";
            }
            return user.formatDate(removeTimestamp);
        }).setHeader("Expires");
        grid.addColumn(createStatusComponentRenderer()).setHeader(createDropdownFilter("Status", Arrays.asList("All", "Active", "Inactive", "Expired", "Removed"), filter::setStatus, (select)-> {
            select.addComponents("Inactive", new Hr());
            select.addComponents("Active", new Hr());
        }));

        layout.add(grid);

        return layout;
    }

    @Getter
    public static class PunishmentFilter {
        private GridListDataView<IPunishment> dataView;
        private String type = ""; // TODO: use enum
        private String reason = "";
        private String issuer = "";
        private String issued = "";
        private String status = ""; // TODO: boolean

        public PunishmentFilter(GridListDataView<IPunishment> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        private boolean test(IPunishment punishment) {
            boolean matchesType = type.isEmpty() || matches(punishment.getType().name(), type) || type.equalsIgnoreCase("all");
            boolean matchesReason = reason.isEmpty() || matches(punishment.getReason(), reason);
            boolean matchesIssuer = issuer.isEmpty() || matches(punishment.getAddedByName(), issuer) || matches(punishment.getAddedBy().toString(), issuer);
            boolean matchesStatus = status.isEmpty() || matches(punishment.getStatusText(), status) || status.equalsIgnoreCase("all") || (status.equalsIgnoreCase("inactive") && !punishment.isActive());
            if (status.equalsIgnoreCase("inactive")) {
                matchesStatus = !punishment.isActive();
            }
            return matchesType && matchesReason && matchesIssuer && matchesStatus;
        }

        private boolean matches(String value, String searchTerm) {
            return searchTerm == null || searchTerm.isEmpty()
                    || value.toLowerCase().contains(searchTerm.toLowerCase());
        }

        public void setDataView(GridListDataView<IPunishment> dataView) {
            this.dataView = dataView;
        }

        public void setType(String type) {
            this.type = type;
            dataView.refreshAll();
        }

        public void setReason(String reason) {
            this.reason = reason;
            dataView.refreshAll();
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
            dataView.refreshAll();
        }

        public void setIssued(String issued) {
            this.issued = issued;
            dataView.refreshAll();
        }

        public void setStatus(String status) {
            this.status = status;
            dataView.refreshAll();
        }
    }


    private static Component createFilterType(String labelText,
                                              Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    private static Component createDropdownFilter(String labelText, List<String> items, Consumer<String> filterChangeConsumer, Consumer<Select<String>>... modifySelect) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        Select<String> select = new Select();
        select.setItems(items);

        select.setWidthFull();
        select.getStyle().set("max-width", "100%");
        select.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        for (Consumer<Select<String>> consumer : modifySelect) {
            consumer.accept(select);
        }
        VerticalLayout layout = new VerticalLayout(label, select);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }
    public static Component createDatePicker(String labelText, Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        DatePicker datePicker = new DatePicker();
        //datePicker.setValue(LocalDate.now());
        datePicker.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue().toString()));
        VerticalLayout layout = new VerticalLayout(label, datePicker);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }


    private static final SerializableBiConsumer<Span, IPunishment> statusComponentUpdater = (
            span, punishment) -> {
        boolean isActive = punishment.isActive();
        String theme = String.format("badge %s",
                !isActive ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(punishment.getStatusText());
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
