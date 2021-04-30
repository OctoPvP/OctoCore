package net.octopvp.octocore.paper.staff.staffchat;

import com.google.gson.JsonObject;

public class StaffChat  {
    public void handleIncoming(JsonObject object) {
        String sender = object.get("sender").getAsString();
        String message = object.get("message").getAsString();
        String server = object.get("server").getAsString();
    }
}
