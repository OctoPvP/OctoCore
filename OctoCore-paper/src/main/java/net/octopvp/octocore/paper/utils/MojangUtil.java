package net.octopvp.octocore.paper.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MojangUtil {
    public static CompletableFuture<Boolean> doesPlayerExist(String username){
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        Tasks.runAsync(()->{
            try {
                completableFuture.complete(readStringFromURL("https://api.mojang.com/users/profiles/minecraft/" + username).contains("name"));
            } catch (IOException e) {
                e.printStackTrace();
                //assume they don't
                completableFuture.complete(false);
            }
        });
        return completableFuture;
    }
    public static CompletableFuture<String> uuidToName(UUID uuid1){
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        Tasks.runAsync(()->{
            try{
                URL url = new URL( "https://api.mojang.com/user/profiles/" + uuid1.toString().replace("-", "") + "/names");
                String response = readStringFromURL(url.toString());
                Logger.debug(response);
                JsonArray namev = new JsonParser().parse(response).getAsJsonArray();
                String slot = namev.get(namev.size() - 1).toString();
                JsonObject nameObject = new JsonParser().parse(slot).getAsJsonObject();
                completableFuture.complete(nameObject.get("name").toString().replace("\"",""));
            } catch (IOException e) {
                e.printStackTrace();
                completableFuture.complete("Error Resolving Name");
            }
        });
        return completableFuture;
    }

    public static CompletableFuture<UUID> nameToUUID(String name){
        CompletableFuture<UUID> completableFuture = new CompletableFuture<>();
        Tasks.runAsync(()->{
            try {
                String response = readStringFromURL("https://api.mojang.com/users/profiles/minecraft/" + name);
                JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                String uuid = jsonObject.get("id").getAsString();
                UUID uuid1 = UUIDUtil.addDashes(uuid);
                completableFuture.complete(uuid1);
            } catch (IOException e) {
                e.printStackTrace();
                completableFuture.complete(null);
            }
        });
        return completableFuture;
    }

    private static String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
