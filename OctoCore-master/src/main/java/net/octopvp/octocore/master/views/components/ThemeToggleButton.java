package net.octopvp.octocore.master.views.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.Lumo;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;

import java.util.Objects;

public class ThemeToggleButton extends Button {
    private MongoUserRepository mongoUserRepository;
    private UserService userService;
    private String userID;

    public ThemeToggleButton(MongoUserRepository mongoUserRepository, UserService userService, String userID) {
        this.mongoUserRepository = mongoUserRepository;
        this.userID = userID;
        this.userService = userService;
        addClickListener(click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (mongoUserRepository == null) {
                if (themeList.contains(Lumo.DARK)) {
                    themeList.remove(Lumo.DARK);
                    setIcon(VaadinIcon.MOON_O.create());
                } else {
                    themeList.add(Lumo.DARK);
                    setIcon(VaadinIcon.SUN_O.create());
                }
                updateIcon();
                return;
            }
            User u = Objects.requireNonNull(mongoUserRepository.findByUserID(userID).orElse(null));
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
                setIcon(VaadinIcon.MOON_O.create());
                u.setDarkMode(false);
            } else {
                themeList.add(Lumo.DARK);
                setIcon(VaadinIcon.SUN_O.create());
                u.setDarkMode(true);
            }
            boolean dark = u.isDarkMode();
            // change the logo id to the correct one
            String url = MainLayout.getLogoURL(dark);
            UI.getCurrent().getElement().executeJs("document.getElementById('logo').src = $0", url);
            mongoUserRepository.save(u);
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (mongoUserRepository == null) {
            UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
            updateIcon();
            return;
        }
        User user = userService.get();
        if (user != null) {
            if (user.isDarkMode()) {
                UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
            } else {
                UI.getCurrent().getElement().getThemeList().remove(Lumo.DARK);
            }
        }

        updateIcon();
    }

    public void updateIcon() {
        boolean currentlyInDarkMode = UI.getCurrent().getElement().getThemeList().contains(Lumo.DARK);
        Icon icon = new Icon(currentlyInDarkMode ? VaadinIcon.SUN_O : VaadinIcon.MOON_O);
        setIcon(icon);
    }
}
