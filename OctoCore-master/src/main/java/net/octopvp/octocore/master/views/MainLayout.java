package net.octopvp.octocore.master.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.components.ThemeToggleButton;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Main")
public class MainLayout extends AppLayout {

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames("font-medium", "text-s");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {
            public LineAwesomeIcon(String lineawesomeClassnames) {
                // Use Lumo classnames for suitable font size and margin
                addClassNames("me-s", "text-l");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }

    }

    private H1 viewTitle;

    private UserService authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    private MongoUserRepository mongoUserRepository;

    public MainLayout(UserService authenticatedUser, AccessAnnotationChecker accessChecker, MongoUserRepository mongoUserRepository) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.mongoUserRepository = mongoUserRepository;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        User user = authenticatedUser.get();
        addToDrawer(createDrawerContent(user.isDarkMode()));
    }


    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
        return header;
    }

    private Component createDrawerContent(boolean dark) {
        Div top = new Div();
        Image logo = new Image(getLogoURL(dark), "OctoPvP Logo");
        logo.setId("logo");
        logo.setHeight("40px");
        logo.setWidth("40px");
        H2 appName = new H2("OctoCore");
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");
        top.add(logo, appName);
        //appName.add(logo);
        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(
                top,
                createNavigation(),
                createFooter()
        );
        section.addClickListener(click -> {
            // redirect to /
            UI.getCurrent().navigate("");
        });
        section.addClassNames("noselect", "pointer-hover");
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }
    public static String getLogoURL(boolean dark) {
        //return dark ? "https://cdn.carbonhost.cloud/6201479d7b237373ab269385/assets/launchpad/launchpad-transparent-dark.png" : "https://cdn.carbonhost.cloud/6201479d7b237373ab269385/assets/launchpad/launchpad-transparent-light.png";
        return "/img/logo.png";
    }
    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("list-none", "m-0", "p-0");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("Hello World", "la la-globe", HelloWorldView.class),
                new MenuItemInfo("Test", "la la-globe", AnotherTestView.class),
        };
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

        User user = authenticatedUser.get();
        if (user != null) {
            Avatar avatar = new Avatar(user.getUsername(), user.getProfilePictureURL());
            avatar.addClassNames("me-xs", "pointer-hover");

            Span name = new Span(user.getUsername());
            name.addClassNames("font-medium", "text-s", "text-secondary", "noselect", "pointer-hover");

            List<ContextMenu> contextMenus = new ArrayList<>();
            contextMenus.add(new ContextMenu(avatar));
            contextMenus.add(new ContextMenu(name));

            for (ContextMenu contextMenu : contextMenus) { // Jfc div doesn't work
                contextMenu.setOpenOnClick(true);
                contextMenu.addItem("Logout", e -> {
                    authenticatedUser.logout();
                });
            }

            layout.add(avatar, name);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }
        ThemeToggleButton toggleButton = new ThemeToggleButton(mongoUserRepository,authenticatedUser,user.getUserID());
        // align to the right side
        toggleButton.getElement().getStyle().set("margin-left", "auto");
        layout.add(toggleButton);
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
