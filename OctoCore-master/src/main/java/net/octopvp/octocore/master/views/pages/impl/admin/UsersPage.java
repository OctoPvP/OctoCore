package net.octopvp.octocore.master.views.pages.impl.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.pages.Page;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.stream.Collectors;

@PageTitle("Users")
@Route(value = "admin/users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersPage extends Page {
    @Autowired
    private MongoUserRepository userRepository;

    @Override
    public void init() {
        VirtualList<User> list = new VirtualList<>();
        list.setItems(userRepository.findAll());
        list.setRenderer(userCardRenderer);
        add(list);
    }

    private ComponentRenderer<Component, User> userCardRenderer = new ComponentRenderer<>(
            user -> {
                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                Avatar avatar = new Avatar(user.getUsername(), user.getProfilePictureURL());
                avatar.setHeight("64px");
                avatar.setWidth("64px");

                VerticalLayout infoLayout = new VerticalLayout();
                infoLayout.setSpacing(false);
                infoLayout.setPadding(false);
                infoLayout.getElement().appendChild(
                        ElementFactory.createStrong(user.getUsername()));
                //infoLayout.add(new Div(new Text(person.getProfession())));

                VerticalLayout contactLayout = new VerticalLayout();
                contactLayout.setSpacing(false);
                contactLayout.setPadding(false);
                String email = user.getEmail();
                contactLayout.add(new Div(new Text(email == null || email.isEmpty() ? "No email" : email)));
                //contactLayout
                //        .add(new Div(new Text(person.getAddress().getPhone())));
                infoLayout.add(new Div(new Text("Roles: " +
                        user.getRoles().stream().map(role -> StringUtils.capatalizeFirst(role.replace("ROLE_", ""))).collect(Collectors.joining(", "))
                )));
                infoLayout
                        .add(new Details("Contact information", contactLayout));

                cardLayout.add(avatar, infoLayout);
                return cardLayout;
            }
    );
}
