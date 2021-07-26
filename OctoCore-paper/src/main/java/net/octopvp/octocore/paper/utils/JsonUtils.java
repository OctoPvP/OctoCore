package net.octopvp.octocore.paper.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class JsonUtils {
    /**
     * Merges 2 jsonobjects into 1 (replaces any objects that exist in json1)
     * @param json1 - what is returned, all values in json2 are replaced/added in this
     * @param json2 - what is to be added/replaced
     * @return both values merged
     */
    public static JsonObject merge(JsonObject json1, JsonObject json2){
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : json2.entrySet()) {
            String entry = stringJsonElementEntry.getKey();
            JsonElement jsonElement = stringJsonElementEntry.getValue();
            if (json1.has(entry))
                json1.remove(entry);
            json1.add(entry,jsonElement);
        }
        return json1;
    }
}
