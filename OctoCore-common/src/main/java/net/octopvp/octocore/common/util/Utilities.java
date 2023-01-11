package net.octopvp.octocore.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import oshi.SystemInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utilities {
    private static final Pattern UUID_DASH_PATTERN = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)"),
    UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    private static SystemInfo systemInfo;

    public static void init() {
        systemInfo = new SystemInfo();
    }

    public static SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public static boolean isUUID(String in) {
        return UUID_PATTERN.matcher(in).matches();
    }

    public static String convertToHumanReadableFromMillis(long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm");
        Date date = new Date(mills);
        return sdf.format(date);
    }

    public static String hashString(String in) {
        String generatedString = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(in.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedString = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedString;
    }

    public static int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static String genRandomString(int targetStringLength) {
        //int leftLimit = 97; // letter 'a'
        //int rightLimit = 122; // letter 'z'
        int leftLimit = 'a', rightLimit = 'z';
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    /**
     * Get the max memory allocated to the jvm.
     *
     * @return The max memory allocated to the jvm in mb
     */
    public static long getMaxMemoryAllocated() {
        return Runtime.getRuntime().maxMemory() / 1024L / 1024L;
    }

    public static UUID addDashes(String unformatted) { // ty https://stackoverflow.com/questions/18986712/creating-a-uuid-from-a-string-with-no-dashes
        /*
        return UUID.fromString(
                unformatted
                        .replaceFirst(
                                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)" , "$1-$2-$3-$4-$5"
                        )
        );
         */
        return UUID.fromString(UUID_DASH_PATTERN.matcher(unformatted).replaceFirst("$1-$2-$3-$4-$5"));
    }

    /**
     * Merges 2 jsonobjects into 1 (replaces any objects that exist in json1)
     *
     * @param json1 - what is returned, all values in json2 are replaced/added in this
     * @param json2 - what is to be added/replaced
     * @return both values merged
     */
    public static JsonObject merge(JsonObject json1, JsonObject json2) {
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : json2.entrySet()) {
            String entry = stringJsonElementEntry.getKey();
            JsonElement jsonElement = stringJsonElementEntry.getValue();
            if (json1.has(entry))
                json1.remove(entry);
            json1.add(entry, jsonElement);
        }
        return json1;
    }
}
