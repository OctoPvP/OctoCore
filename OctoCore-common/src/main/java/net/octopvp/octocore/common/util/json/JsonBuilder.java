package net.octopvp.octocore.common.util.json;

import com.google.gson.JsonObject;

public class JsonBuilder {

    private JsonObject json = new JsonObject();

    public JsonBuilder addProperty(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonObject get() {
        return this.json;
    }

}
