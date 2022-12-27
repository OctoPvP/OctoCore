package net.octopvp.octocore.core.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GsonSerializer {
    public static String serializeUUIDSet(Set<UUID> uuids) {
        JsonArray array = new JsonArray();
        for (UUID uuid : uuids) {
            array.add(new JsonPrimitive(uuid.toString()));
        }
        return array.toString();
    }

    public static Set<UUID> deserializeUUIDSet(String json) {
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        Set<UUID> set = new HashSet<>();
        for (int i = 0; i < array.size(); i++) {
            set.add(UUID.fromString(array.get(i).getAsString()));
        }
        return set;
    }
}
