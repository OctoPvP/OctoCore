package net.octopvp.octocore.common.util;

import org.bson.Document;

public class DocumentUtils {
    public static long getLong(Document doc, String key, long... def) {
        Number number = getNumber(doc, key);
        if (number == null) return def.length > 0 ? def[0] : -1L;
        return number.longValue();
    }

    public static int getInt(Document doc, String key, int... def) {
        Number number = getNumber(doc, key);
        if (number == null) return def.length > 0 ? def[0] : -1;
        return number.intValue();
    }

    public static double getDouble(Document doc, String key, double... def) {
        Number n = getNumber(doc, key);
        if (n == null) return def.length > 0 ? def[0] : -1;
        return n.doubleValue();
    }

    public static Number getNumber(Document doc, String key) {
        Object obj = doc.get(key);
        if (obj != null && obj instanceof Number) return (Number) obj;
        else return null;
    }

}
