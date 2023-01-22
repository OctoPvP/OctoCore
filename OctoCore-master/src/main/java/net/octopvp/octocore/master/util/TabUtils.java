package net.octopvp.octocore.master.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabUtils {
    public static void persistSelection(TabSheet tabSheet, Location location) {
        Map<String, List<String>> param = location.getQueryParameters().getParameters();
        if (param.containsKey("tab") && param.get("tab").size() > 0) {
            String tab = param.get("tab").get(0);
            try {
                int tabIndex = Integer.parseInt(tab) - 1;
                tabSheet.setSelectedIndex(tabIndex);
            } catch (NumberFormatException ignored) {
                // ignored
            }
        }

        tabSheet.addSelectedChangeListener(event -> {
            // set the query parameter
            Map<String, List<String>> currentParameters = new HashMap<>(location.getQueryParameters().getParameters());
            currentParameters.put("tab", List.of(String.valueOf(tabSheet.getIndexOf(event.getSelectedTab()) + 1)));
            String query = new QueryParameters(currentParameters).getQueryString();
            if (query.startsWith("&")) {
                query = query.substring(1);
            }
            UI.getCurrent().getPage().executeJs("window.history.replaceState({}, '', $0)", location.getPath() + (query.isEmpty() ? "" : "?" + query));
        });
    }
}
