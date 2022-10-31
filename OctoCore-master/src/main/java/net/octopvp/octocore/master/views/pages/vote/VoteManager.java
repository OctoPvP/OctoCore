package net.octopvp.octocore.master.views.pages.vote;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vexsoftware.votifier.model.Vote;
import net.octopvp.octocore.master.component.LightningHolder;
import net.octopvp.octocore.master.master.votifier.VotifierEvent;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.util.NotificationUtils;

import javax.annotation.security.PermitAll;

@PageTitle("Vote Manager")
@Route(value = "votemgr", layout = MainLayout.class)
@PermitAll
public class VoteManager extends VerticalLayout {
    public VoteManager() {
        TextField usernameField = new TextField();
        usernameField.setLabel("Username");
        usernameField.setRequired(true);
        usernameField.addClassNames("centered");
        TextField voteServiceField = new TextField();
        voteServiceField.setLabel("Vote Service");
        voteServiceField.addClassNames("centered");
        Button button = new Button("Submit");
        button.addClassNames("centered");
        button.addClickListener(event -> {
            String username = usernameField.getValue();
            String voteService = voteServiceField.getValue();
            if (voteService.isEmpty()) {
                voteService = "Test";
            }
            if (username.isEmpty()) {
                NotificationUtils.create("Username is required.", NotificationVariant.LUMO_ERROR).open();
                return;
            }
            NotificationUtils.create("Vote submitted for " + username + " on " + voteService + ".", NotificationVariant.LUMO_SUCCESS).open();
            Vote vote = new Vote(voteService, username, "0.0.0.0", Long.toString(System.currentTimeMillis(), 10));
            LightningHolder.getInstance().getEventBus().callEvent(new VotifierEvent(vote));
        });
        add(usernameField);
    }
}
