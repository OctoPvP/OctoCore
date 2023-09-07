package net.octopvp.octocore.common.util.perms;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.octopvp.octocore.common.object.ServerContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NodeAdapter extends TypeAdapter<Node> {

    private static final Gson plainGson = new Gson();

    @Override
    public void write(JsonWriter jsonWriter, Node node) throws IOException {
        jsonWriter.beginObject()
                .name("key").value(node.getKey());
        node.getNegated().ifPresent(negated -> {
            try {
                jsonWriter.name("negated").value(negated);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        node.getServerContext().ifPresent(serverContext -> {
            try {
                jsonWriter.name("serverContext").jsonValue(plainGson.toJson(serverContext));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        if (node.hasChildren()) {
            jsonWriter.name("children").beginObject();
            for (Map.Entry<String, Node> entry : node.getChildren().entrySet()) {
                try {
                    jsonWriter.name(entry.getKey());
                    write(jsonWriter, entry.getValue());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            jsonWriter.endObject();
        }
        jsonWriter.endObject();
    }

    @Override
    public Node read(JsonReader jsonReader) throws IOException {
        JsonObject object = JsonParser.parseReader(jsonReader).getAsJsonObject();
        return readObject(object);
    }

    public Node readObject(JsonObject object) {
        String key = object.get("key").getAsString();
        Optional<Boolean> negated = Optional.empty();
        Optional<ServerContext> serverContext = Optional.empty();
        if (object.has("negated")) {
            negated = Optional.of(object.get("negated").getAsBoolean());
        }
        if (object.has("serverContext")) {
            serverContext = Optional.of(plainGson.fromJson(object.get("serverContext"), ServerContext.class));
        }
        Node node = new Node(key, null, new HashMap<>(), negated, serverContext);
        if (object.has("children")) {
            JsonObject children = object.get("children").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : children.entrySet()) {
                node.addChild(entry.getKey(), readObject(entry.getValue().getAsJsonObject()));
            }
        }
        return node;
    }
}
