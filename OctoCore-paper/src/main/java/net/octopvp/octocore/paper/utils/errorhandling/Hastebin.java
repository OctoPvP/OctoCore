package net.octopvp.octocore.paper.utils.errorhandling;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class Hastebin {
    public CompletableFuture<String> post(String text, boolean raw) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        new Thread("hastebin"){
            @Override
            public void run() {
                super.run();
                byte[] postData = text.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;

                String requestURL = "https://paste.badbird5907.net/documents";
                URL url = null;
                try {
                    url = new URL(requestURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection conn = null;
                try {
                    conn = (HttpsURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                try {
                    conn.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setRequestProperty("User-Agent", "Hastebin Java Api");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setUseCaches(false);

                String response = null;
                DataOutputStream wr;
                try {
                    wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(postData);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    response = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (response.contains("\"key\"")) {
                    response = response.substring(response.indexOf(":") + 2, response.length() - 2);

                    String postURL = raw ? "https://paste.badbird5907.net/raw/" : "https://paste.badbird5907.net/";
                    response = postURL + response;
                }
                completableFuture.complete(response);
            }
        }.run();
        return completableFuture;
    }
}
