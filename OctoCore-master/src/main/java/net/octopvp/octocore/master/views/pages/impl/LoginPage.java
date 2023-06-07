package net.octopvp.octocore.master.views.pages.impl;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.stefan.table.Table;
import org.vaadin.stefan.table.TableRow;

// @PageTitle("Login")
// @Route(value = "login")
// @AnonymousAllowed
public class LoginPage extends VerticalLayout {

    @Value("${master.saml.discovery}")
    private String idps;
    public LoginPage() {
        add(
                new H1("Login to OctoCore"),
                new H2("Please select your identity provider:"),
                new H3("Please contact an administrator for more info."),
                new IdpSelection(idps.split(","))
        );
    }
    public static class IdpSelection extends VerticalLayout {
        public IdpSelection(String[] idps) {
            // make a table of buttons
            Table idpTable = new Table();
            for (String idp : idps) {
                TableRow row = idpTable.addRow();
                row.addCells(new Anchor("/saml2/authenticate/" + idp, idp));
            }
        }
    }
}
