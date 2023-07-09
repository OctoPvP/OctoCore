package net.octopvp.octocore.master.views.pages.impl.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.master.models.Role;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.repository.RoleRepository;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;
import net.octopvp.octocore.master.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Users")
@Route(value = "admin/users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersPage extends Page {
    @Autowired
    private MongoUserRepository userRepository;
    private final TextField searchField = new TextField();
    private GridListDataView<User> dataView;

    @Autowired
    private RoleRepository roleRepository;

    private Grid<User> grid;

    private TextField usernameField, editUsernameField;
    private EmailField emailField, editEmailField;

    private MultiSelectComboBox<Role> roles, editRoles;
    @Autowired
    private UserService authenticatedUser;

    @Override
    public void init() {
        grid = new Grid<>(User.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        dataView = grid.setItems(userRepository.findAll());
        grid.addColumn(userCardRenderer).setHeader("User");
        grid.addColumn(user -> user.getEmail() == null || user.getEmail().isEmpty() ? "No email" : user.getEmail()).setHeader("Email").setAutoWidth(true);
        grid.addColumn(user -> user.getActualRoles().stream().map(role -> StringUtils.capatalizeFirst(role.getName().replace("ROLE_", ""))).collect(Collectors.joining(", "))) // getActualRoles() only returns roles that the user has, not the roles that the user inherits from other roles
                .setHeader("Roles"); // TODO order roles by priority
        grid.addColumn(user -> user.getTimeZone().getDisplayName()).setHeader("Timezone").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(Div::new, (div, user) -> {
            Button button = new Button();
            button.setIcon(new Icon(VaadinIcon.EDIT));
            button.addClickListener(event -> {
                // redirect to https://auth.octomc.net/if/admin/#/identity/users/<id>
                String idpId = user.getIdpID();
                if (idpId == null || idpId.isEmpty()) {
                    NotificationUtils.createConfirmDialog("No IDP ID", "This user does not have an IDP ID, so they cannot be edited.").open();
                    return;
                }
                getUI().ifPresent(ui -> ui.getPage().setLocation("https://auth.octomc.net/if/admin/#/identity/users/" + idpId));
            });
            String idpId = user.getIdpID();
            if (idpId == null || idpId.isEmpty()) {
                // show a popover
                Tooltip.forComponent(button).setText("This user is not linked to the identity provider!");
                button.setEnabled(false);
            }
            div.add(button);
            Button deleteButton = new Button();
            deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
            deleteButton.addClickListener(event -> {
                ConfirmDialog confirmDialog = NotificationUtils.createConfirmDialog("Delete User", "Are you sure you want to delete this user?");
                confirmDialog.addConfirmListener(event1 -> {
                    userRepository.delete(user);
                    dataView.refreshAll();
                });
                confirmDialog.open();
            });
            div.add(deleteButton);
        })).setHeader("Actions").setAutoWidth(true);

        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("New User");

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton(dialog); //TODO perm checking
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        Button createButton = new Button("Create User", new Icon(VaadinIcon.PLUS));

        createButton.addClickListener(event -> dialog.open());

        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.setEnabled(false);
        deleteButton.addClickListener(event -> {
            Set<User> selected = grid.getSelectedItems();
            if (selected.isEmpty()) {
                NotificationUtils.createConfirmDialog("No users selected", "Please select at least one user to delete.").open();
                return;
            }
            // Check roles/perms
            User currentUser = authenticatedUser.get();
            int highestRole = currentUser.getHighestRolePriority();
            for (User user : selected) {
                if (user.getHighestRolePriority() >= highestRole) {
                    NotificationUtils.createConfirmDialog("Insufficient permissions", "You do not have permission to delete user \"" + user.getUsername() + "\"").open();
                    return;
                }
            }

            userRepository.deleteAll(selected);
            ConfirmDialog d = NotificationUtils.createConfirmDialog("Users deleted", "The selected users have been deleted.");
            d.addConfirmListener(e -> dataView.refreshAll());
            d.open();
        });

        dataView.addFilter(user -> {
            String searchTerm = searchField.getValue().trim().toLowerCase();

            if (searchTerm.isEmpty())
                return true;

            return user.getUsername().toLowerCase().contains(searchTerm) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchTerm)) ||
                    user.getRoles().stream().anyMatch(role -> role.getName().toLowerCase().contains(searchTerm)) ||
                    user.getTimeZone().getDisplayName().toLowerCase().contains(searchTerm);
        });
        grid.addSelectionListener((SelectionListener<Grid<User>, User>) event -> deleteButton.setEnabled(!event.getAllSelectedItems().isEmpty()));
        HorizontalLayout actions = new HorizontalLayout(searchField, createButton, deleteButton); // TODO: delete button
        actions.setMaxWidth("100%");
        actions.setWidth("100%");
        add(actions, grid, dialog);
    }

    private Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button("Submit", e -> {
            boolean error = false;
            // check validity
            if (usernameField.getValue().isEmpty()) {
                usernameField.setInvalid(true);
                usernameField.setErrorMessage("Username cannot be empty");
                error = true;
            }
            if (emailField.getValue().isEmpty()) {
                emailField.setInvalid(true);
                emailField.setErrorMessage("Email cannot be empty");
                error = true;
            }
            if (roles.getValue().isEmpty()) {
                roles.setInvalid(true);
                roles.setErrorMessage("Roles cannot be empty");
                error = true;
            }
            if (error) return;

            String username = usernameField.getValue();
            if (userRepository.existsByUsernameIgnoreCase(username)) {
                usernameField.setInvalid(true);
                usernameField.setErrorMessage("Username already exists");
                return;
            }
            String email = emailField.getValue();
            if (!StringUtils.isEmailValid(email)) {
                emailField.setInvalid(true);
                emailField.setErrorMessage("Email is invalid");
                return;
            }

            createUser();
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }

    private VerticalLayout createDialogLayout() {
        usernameField = new TextField("Username");
        emailField = new EmailField("Email");
        roles = new MultiSelectComboBox<>();
        roles.setLabel("Roles");
        List<Role> roleList = roleRepository.findAll();
        roles.setItems(roleList.toArray(new Role[0]));
        roles.setPlaceholder("Select roles");
        roles.setItemLabelGenerator(role -> StringUtils.capatalizeFirst(role.getName().replace("ROLE_", "")));

        VerticalLayout dialogLayout = new VerticalLayout(usernameField,
                emailField, roles);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    public void createUser() {
        User user = new User();
        user.setUsername(usernameField.getValue());
        user.setEmail(emailField.getValue());
        user.setRoles(new HashSet<>(roles.getValue())); // TODO default, unremovable user role
        userRepository.save(user);
        grid.setItems(userRepository.findAll());
        dataView.refreshAll();
    }

    private static final ComponentRenderer<Component, User> userCardRenderer = new ComponentRenderer<>(
            user -> {
                HorizontalLayout layout = new HorizontalLayout();
                layout.setAlignItems(Alignment.CENTER);
                layout.setSpacing(true);
                layout.add(new Avatar(user.getUsername(), user.getProfilePictureURL()));
                VerticalLayout verticalLayout = new VerticalLayout();
                verticalLayout.add(new Text(user.getUsername()));
                layout.add(verticalLayout);
                return layout;
            }
    );
}
