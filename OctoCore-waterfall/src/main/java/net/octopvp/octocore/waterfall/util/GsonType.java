package net.octopvp.octocore.waterfall.util;

import com.google.gson.reflect.TypeToken;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.util.permissions.Node;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GsonType {
    public static final Type NODE_LIST = new TypeToken<List<Node>>() {
    }.getType();
    public static final Type OBJECT = new TypeToken<Object>() {
    }.getType();
    public static final Type STRING_LIST = new TypeToken<List<String>>() {
    }.getType();
    public static final Type UUID_SET = new TypeToken<HashSet<UUID>>() {
    }.getType();
    public static final Type STRING_STRING_MAP = new TypeToken<Map<String, String>>() {
    }.getType();
    public static final Type STRING_SERVER_CONTEXT_MAP = new TypeToken<Map<String, ServerContext>>() {

    }.getType();
}
