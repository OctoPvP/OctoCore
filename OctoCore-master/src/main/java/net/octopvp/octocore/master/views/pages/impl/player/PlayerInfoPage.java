package net.octopvp.octocore.master.views.pages.impl.player;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.shared.Tooltip;
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
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.punish.PunishData;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.Utilities;
import net.octopvp.octocore.master.master.manager.PlayerManager;
import net.octopvp.octocore.master.master.object.MasterPunishment;
import net.octopvp.octocore.master.master.util.AccountUtil;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.util.TabUtils;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.components.FloatingActionButton;
import net.octopvp.octocore.master.views.components.PlayerName;
import net.octopvp.octocore.master.views.pages.Page;
import net.octopvp.octocore.master.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    private MongoUserRepository userRepository;
    private User user;
    private UUID uuid;
    private String name;
    private Location location;
    private FloatingActionButton fab;

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        user = userService.get();
        uuid = UUID.fromString(parameter);
        name = accountUtil.getName(uuid);
        location = event.getLocation();
        populate(location);
    }

    public void populate(Location location) {
        Button refresh = new Button(VaadinIcon.REFRESH.create());
        HorizontalLayout title = new HorizontalLayout(new PlayerName(name, true), refresh);
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

        tabSheet.addSelectedChangeListener(event -> {
            String label = event.getSelectedTab().getLabel();
            if (fab != null) remove(fab);
            if (label.equalsIgnoreCase("punishments")) {
                add(getPunishmentsFab(punishData));
            }
        });

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
        //grid.setSelectionMode(Grid.SelectionMode.MULTI);
        GridListDataView<IPunishment> dataView = grid.setItems(data.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).toList());
        PunishmentFilter filter = new PunishmentFilter(dataView);

        grid.addColumn(punish -> StringUtils.capatalizeFirstDeep(punish.getType().name())).setHeader(createDropdownFilter("Type", Utilities.addAllNewList(PunishmentType.getNames(), "All"), filter::setType, (select) -> {
            select.addComponents(PunishmentType.getNames().get(PunishmentType.getNames().size() - 1), new Hr());
        }));
        grid.addColumn(IPunishment::getReason).setHeader(createFilterType("Reason", filter::setReason));
        grid.addColumn(punisherRenderer()).setHeader(createFilterType("Punisher", filter::setIssuer));
        grid.addColumn(punish -> user.formatDate(punish.getAddedAt())).setHeader(createDateRangePicker("Issued", filter::setIssuedEnd, filter::setIssuedStart));
        grid.addColumn(punish -> {
            long removeTimestamp = punish.getRemoveTimestamp();
            if (removeTimestamp < 0) {
                return "Never";
            }
            return user.formatDate(removeTimestamp);
        }).setHeader(createDateRangePicker("Expire", filter::setExpiresEnd, filter::setExpiresStart));
        grid.addColumn(removedRenderer()).setHeader(createFilterType("Removed By", filter::setRemover));
        grid.addColumn(createStatusComponentRenderer()).setHeader(createDropdownFilter("Status", Arrays.asList("All", "Active", "Inactive", "Expired", "Removed"), filter::setStatus, (select) -> {
            select.addComponents("Inactive", new Hr());
            select.addComponents("All", new Hr());
        }));

        Button revoke = new Button("Revoke", VaadinIcon.CLOSE.create());
        revoke.setEnabled(false);
        revoke.addThemeVariants(ButtonVariant.LUMO_ERROR);
        revoke.getStyle().set("margin-inline-start", "auto");

        HorizontalLayout footer = new HorizontalLayout(revoke);
        footer.getStyle().set("flex-wrap", "wrap");

        grid.addSelectionListener(event -> {
            revoke.setEnabled(event.getFirstSelectedItem().isPresent() && event.getFirstSelectedItem().get().isActive());
        });

        revoke.addClickListener(event -> {
            if (grid.getSelectedItems().isEmpty()) return;
            List<IPunishment> punishments = new ArrayList<>(grid.getSelectedItems());
            IPunishment punishment = punishments.get(0);
            if (!punishment.isActive()) {
                NotificationUtils.create("This punishment is already inactive.", NotificationVariant.LUMO_ERROR).open();
                return;
            }
            // TODO finish

        });

        layout.add(grid, footer);

        return layout;
    }

    @Getter
    public static class PunishmentFilter {
        private GridListDataView<IPunishment> dataView;
        private String type = ""; // TODO: use enum
        private String reason = "";
        private String issuer = "";
        private String status = ""; // TODO: boolean
        private String remover = "";
        private LocalDate issuedStart, issuedEnd, expiresStart, expiresEnd;

        public PunishmentFilter(GridListDataView<IPunishment> dataView) {
            this.dataView = dataView;
            this.dataView.addFilter(this::test);
        }

        private boolean test(IPunishment punishment) {
            boolean matchesType = type.isEmpty() || matches(punishment.getType().name(), type) || type.equalsIgnoreCase("all");
            boolean matchesReason = reason.isEmpty() || matches(punishment.getReason(), reason);
            boolean matchesIssuer = issuer.isEmpty() ||
                    matches(punishment.getAddedByName(), issuer) ||
                    matches(punishment.getAddedBy().toString(), issuer) ||
                    (punishment.getWebPanelName() != null &&
                            matches(punishment.getWebPanelName(), issuer));
            boolean matchesStatus = status.isEmpty() || matches(punishment.getStatusText(), status) || status.equalsIgnoreCase("all") || (status.equalsIgnoreCase("inactive") && !punishment.isActive());
            boolean matchesRemover = remover.isEmpty() || matches(punishment.getRemovedBy(), remover) || (punishment.getRemovedOnWebPanelName() != null && matches(punishment.getRemovedOnWebPanelName(), remover));
            if (status.equalsIgnoreCase("inactive")) {
                matchesStatus = !punishment.isActive();
            }

            if (issuedStart != null) {
                long addedAtLong = punishment.getAddedAt();
                LocalDate addedAt = LocalDate.ofEpochDay(addedAtLong / 86400000);
                if (addedAt.isBefore(issuedStart)) {
                    return false;
                }
            }
            if (issuedEnd != null) {
                long removeTimestamp = punishment.getRemoveTimestamp();
                LocalDate removeDate = removeTimestamp <= 0 ? null : LocalDate.ofEpochDay(removeTimestamp / 86400000);
                if (removeDate == null || removeDate.isAfter(issuedEnd)) {
                    return false;
                }
            }
            if (expiresStart != null) {
                long removeTimestamp = punishment.getRemoveTimestamp();
                LocalDate removeDate = removeTimestamp <= 0 ? null : LocalDate.ofEpochDay(removeTimestamp / 86400000);
                if (removeDate == null || removeDate.isBefore(expiresStart)) {
                    return false;
                }
            }
            if (expiresEnd != null) {
                long removeTimestamp = punishment.getRemoveTimestamp();
                LocalDate removeDate = removeTimestamp <= 0 ? null : LocalDate.ofEpochDay(removeTimestamp / 86400000);
                if (removeDate == null || removeDate.isAfter(expiresEnd)) {
                    return false;
                }
            }

            return matchesType && matchesReason && matchesIssuer && matchesStatus && matchesRemover;
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
            if (issuer == null) {
                log.info("Issuer is null");
                issuer = "";
            }
            this.issuer = issuer;
            dataView.refreshAll();
        }

        public void setStatus(String status) {
            this.status = status;
            dataView.refreshAll();
        }

        public void setIssuedStart(LocalDate issuedStart) {
            this.issuedStart = issuedStart;
            dataView.refreshAll();
        }

        public void setIssuedEnd(LocalDate issuedEnd) {
            this.issuedEnd = issuedEnd;
            dataView.refreshAll();
        }

        public void setExpiresStart(LocalDate expiresStart) {
            this.expiresStart = expiresStart;
            dataView.refreshAll();
        }

        public void setExpiresEnd(LocalDate expiresEnd) {
            this.expiresEnd = expiresEnd;
            dataView.refreshAll();
        }

        public void setRemover(String s) {
            this.remover = s;
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

    public Component createDateRangePicker(String labelText, Consumer<LocalDate> endDateConsumer, Consumer<LocalDate> startDateConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        Tooltip.forComponent(label).setText("Select a date range.\nMay be off by a day...");
        DatePicker startDate = new DatePicker();
        startDate.setTooltipText("Start date");
        startDate.getStyle().set("width", "30%");
        DatePicker endDate = new DatePicker();
        endDate.getStyle().set("width", "30%");
        endDate.setTooltipText("End date");
        endDate.setMax(LocalDate.now());
        endDate.setInitialPosition(LocalDate.now());
        startDate.addValueChangeListener(e -> {
            endDate.setMin(e.getValue());
            startDate.setTooltipText("Start date: " + user.formatDate(e.getValue()));
            startDateConsumer.accept(e.getValue());
        });
        endDate.addValueChangeListener(e -> {
            startDate.setMax(e.getValue());
            endDate.setTooltipText("End date: " + user.formatDate(e.getValue()));
            endDateConsumer.accept(e.getValue());
        });
        VerticalLayout layout = new VerticalLayout(label, new HorizontalLayout(startDate, endDate));
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    public FloatingActionButton getPunishmentsFab(PunishData punishData) {
        fab = new FloatingActionButton("+");
        fab.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(true);
            dialog.setCloseOnOutsideClick(true);
            dialog.setHeaderTitle("Add Punishment");
            Button saveButton = new Button("Add");
            saveButton.setEnabled(false);
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button cancelButton = new Button("Cancel", e -> dialog.close());
            dialog.getFooter().add(cancelButton);
            dialog.getFooter().add(saveButton);

            Select<String> typeSelect = new Select<>();
            typeSelect.setItems(PunishmentType.getNames());
            Label typeLabel = new Label("Punishment Type");
            typeLabel.getStyle().set("padding-top", "var(--lumo-space-m)")
                    .set("font-size", "var(--lumo-font-size-xs)");

            TextField reasonField = new TextField();
            reasonField.setLabel("Reason");
            reasonField.setPlaceholder("Reason for punishment");

            DateTimePicker durationPicker = new DateTimePicker();
            durationPicker.setLabel("End Date");
            durationPicker.setMin(LocalDateTime.now());
            durationPicker.setVisible(false);

            typeSelect.addValueChangeListener(e -> {
                PunishmentType type = PunishmentType.valueOf(e.getValue());
                boolean hasDuration = type.hasDuration();
                durationPicker.setVisible(hasDuration);
                saveButton.setEnabled(true);
            });

            Checkbox silentCheckbox = new Checkbox("Silent", true);
            Checkbox ip = new Checkbox("IP", false);

            VerticalLayout dialogLayout = new VerticalLayout(typeLabel, typeSelect, reasonField, durationPicker, ip, silentCheckbox);
            dialogLayout.setPadding(false);
            dialogLayout.setSpacing(false);
            dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
            dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
            dialog.add(dialogLayout);

            saveButton.addClickListener(e -> {
                PunishmentType type = PunishmentType.valueOf(typeSelect.getValue());
                String reason = reasonField.getValue();
                boolean silent = silentCheckbox.getValue();
                LocalDateTime duration = durationPicker.getValue();
                if (reason == null || reason.isEmpty()) {
                    reason = "No reason provided.";
                }
                MasterPunishment punishment = new MasterPunishment(type, name, uuid);
                punishment.setReason(reason);
                punishment.setAddedOnWebPanel(true);
                punishment.setWebPanelId(user.getUserID());
                punishment.setWebPanelName(user.getUsername());
                punishment.setSilent(silent);
                if (type.hasDuration() && duration != null) punishment.setEnteredDuration("WEB_PANEL:" + duration);
                UUID addedByID = user.getMinecraftUUID();
                if (addedByID == null) addedByID = new UUID(0, 0);
                punishment.setAddedBy(addedByID);
                punishment.setAddedByName(user.getMinecraftName());
                punishment.setLast(true);
                punishment.setAddedAt(System.currentTimeMillis());
                punishment.setIPRelative(ip.getValue());
                long durationMillis = duration == null ? -1 : duration.atZone(user.getTimeZone().toZoneId()).toInstant().toEpochMilli();
                if (durationMillis != -1L) {
                    punishment.setPermanent(false);
                    punishment.setDurationTime(durationMillis);
                } else {
                    punishment.setPermanent(true);
                    punishment.setDurationTime(-1L);
                }
                punishment.execute(user);
                punishment.save();
                dialog.close();
                removeAll();
                populate(location);
            });
            dialog.open();
        });
        return fab;
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

    private ComponentRenderer<Component, IPunishment> punisherRenderer() {
        return new ComponentRenderer<>(punishment -> {
            HorizontalLayout layout = new HorizontalLayout();
            boolean showMC = true;
            if (punishment.isAddedOnWebPanel()) {
                HorizontalLayout webPanelLayout = new HorizontalLayout();
                User user = userRepository.findById(punishment.getWebPanelId()).orElse(null);
                if (user != null) {
                    Avatar avatar = new Avatar(user.getUsername(), user.getProfilePictureURL());
                    avatar.getStyle().set("margin-right", "var(--lumo-space-xs)");
                    webPanelLayout.add(avatar);
                    webPanelLayout.add(new Text(user.getUsername() + " (WEB)"));
                    webPanelLayout.setAlignItems(FlexComponent.Alignment.CENTER);
                    layout.add(webPanelLayout);
                    showMC = false;
                }
            }
            if (showMC) {
                PlayerName pName = new PlayerName(punishment.getAddedByName(), false, true);
                layout.add(pName);
            }
            return layout;
        });

    }

    private ComponentRenderer<Component, IPunishment> removedRenderer() {
        return new ComponentRenderer<>(punishment -> {
            HorizontalLayout layout = new HorizontalLayout();
            if (punishment.isActive()) {
                layout.add(new Text("N/A"));
                return layout;
            }
            boolean showMC = true;
            if (punishment.isRemovedOnWebPanel()) {
                HorizontalLayout webPanelLayout = new HorizontalLayout();
                User user = userRepository.findById(punishment.getRemovedOnWebPanelId()).orElse(null);
                if (user != null) {
                    Avatar avatar = new Avatar(user.getUsername(), user.getProfilePictureURL());
                    avatar.getStyle().set("margin-right", "var(--lumo-space-xs)");
                    webPanelLayout.add(avatar);
                    webPanelLayout.add(new Text(user.getUsername() + " (WEB)"));
                    webPanelLayout.setAlignItems(FlexComponent.Alignment.CENTER);
                    layout.add(webPanelLayout);
                    showMC = false;
                }
            }
            if (showMC) {
                if (punishment.getRemovedBy() == null) {
                    layout.add(new Text("N/A"));
                    return layout;
                }
                PlayerName pName = new PlayerName(punishment.getRemovedBy(), false, true);
                layout.add(pName);
            }
            return layout;
        });
    }
}
