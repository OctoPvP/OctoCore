package net.octopvp.octocore.common.util.json;

import com.google.gson.JsonObject;

public class JsonBuilder {
    private final JsonObject json;

    public JsonBuilder() {
        this(new JsonObject());
    }

    public JsonBuilder(JsonObject json) {
        this.json = json;
    }

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

    public JsonBuilder add(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder add(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder add(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder add(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonObject get() {
        return this.json;
    }

}
