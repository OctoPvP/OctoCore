package net.octopvp.octocore.master.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.Lumo;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.components.ThemeToggleButton;
import net.octopvp.octocore.master.views.pages.impl.HomeView;
import net.octopvp.octocore.master.views.pages.impl.Servers;
import net.octopvp.octocore.master.views.pages.impl.SettingsPage;
import net.octopvp.octocore.master.views.pages.impl.admin.UsersPage;
import net.octopvp.octocore.master.views.pages.impl.misc.RedisManagerPage;
import net.octopvp.octocore.master.views.pages.impl.misc.vote.VoteManager;
import net.octopvp.octocore.master.views.pages.impl.player.MainPlayerInfoPage;

import java.util.*;

@PageTitle("Main")
public class MainLayout extends AppLayout {
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

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l");

        //User user = authenticatedUser.get();
        //Avatar avatar = new Avatar(user.getUsername(), user.getProfilePictureURL());
        //avatar.addClassNames("ml-auto", "mr-s");
        Header header = new Header(toggle, viewTitle/*, avatar*/);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
        return header;
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
        //ThemeToggleButton toggleButton = new ThemeToggleButton(mongoUserRepository, authenticatedUser, user.getUserID());
        // align to the right side
        //toggleButton.getElement().getStyle().set("margin-left", "auto");
        //layout.add(toggleButton);
        Button button = new Button(VaadinIcon.COG.create());
        button.getElement().getStyle().set("margin-left", "auto");
        button.setTooltipText("Settings");
        button.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> UI.getCurrent().navigate(SettingsPage.class));
        layout.add(button);
        return layout;
    }

    private Component createDrawerContent(boolean dark) {
        Div top = new Div();
        H2 appName = new H2("OctoCore");
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");
        appName.addClickListener(e -> UI.getCurrent().navigate(HomeView.class));
        top.add(appName);

        //appName.add(logo);
        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(
                top,
                createNavigation(),
                createFooter()
        );
        /*
        section.addClickListener(click -> {
            // redirect to /
            UI.getCurrent().navigate("");
        });
         */
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

        /*
        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }
        }
         */
        Map<String, MenuItemInfo[]> menuItems = createMenuItems();
        // Category, menu item
        Map<String, List<MenuItemInfo>> newMap = new HashMap<>();
        for (Map.Entry<String, MenuItemInfo[]> entry : menuItems.entrySet()) {
            String name = entry.getKey();
            MenuItemInfo[] items = entry.getValue();
            for (MenuItemInfo item : items) {
                if (accessChecker.hasAccess(item.getView())) {
                    if (newMap.containsKey(name)) {
                        newMap.get(name).add(item);
                    } else {
                        List<MenuItemInfo> list1 = new ArrayList<>();
                        list1.add(item);
                        newMap.put(name, list1);
                    }
                }
            }
        }
        for (Map.Entry<String, List<MenuItemInfo>> entry : newMap.entrySet()) {
            String name = entry.getKey();
            if (name == null || name.isEmpty()) {
                entry.getValue().forEach(list::add);
                continue;
            }
            List<MenuItemInfo> items = entry.getValue();
            Accordion accordion = new Accordion();
            accordion.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
            // Add 20px padding on the left
            accordion.getElement().getStyle().set("padding-left", "15px");
            // remove the default border
            accordion.getElement().getStyle().set("border", "none");
            Component itemsComponent = createItemsComponent(items);
            accordion.add(name, itemsComponent);
            list.add(accordion);
        }
        return nav;
    }

    private Component createItemsComponent(List<MenuItemInfo> items) {
        Div div = new Div();
        div.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        for (MenuItemInfo item : items) {
            div.add(item);
        }
        return div;
    }

    private Map<String, MenuItemInfo[]> createMenuItems() { // https://icons8.com/line-awesome
        Map<String, MenuItemInfo[]> menuItems = new LinkedHashMap<>();
        menuItems.put("", new MenuItemInfo[]{
                new MenuItemInfo("Home", "la la-home", HomeView.class),
                new MenuItemInfo("Servers", "la la-server", Servers.class),
        });
        menuItems.put("Misc", new MenuItemInfo[]{
                new MenuItemInfo("Vote Manager", "la la-vote-yea", VoteManager.class),
                new MenuItemInfo("Redis Manager", "la la-exchange-alt", RedisManagerPage.class),
        });
        menuItems.put("Player", new MenuItemInfo[]{
                new MenuItemInfo("Player Info", "la la-user", MainPlayerInfoPage.class),
        });
        menuItems.put("Admin", new MenuItemInfo[]{
                new MenuItemInfo("Users", "la la-users", UsersPage.class),
        });
        return menuItems;
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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (mongoUserRepository == null) {
            UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
            return;
        }
        User user = authenticatedUser.get();
        if (user != null) {
            if (user.isDarkMode()) {
                UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
            } else {
                UI.getCurrent().getElement().getThemeList().remove(Lumo.DARK);
            }
        }
    }
}
