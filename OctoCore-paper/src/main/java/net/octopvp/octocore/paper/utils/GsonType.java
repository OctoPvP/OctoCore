package net.octopvp.octocore.paper.utils;

import com.google.gson.reflect.TypeToken;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.paper.module.impl.punishments.util.Alt;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.objects.permissions.Grant;

import java.lang.reflect.Type;
import java.util.*;

public class GsonType {
    public static final Type GRANT = new TypeToken<ArrayList<Grant>>() {
    }.getType();
    public static final Type NODE_LIST = new TypeToken<List<Node>>() {
    }.getType();
    public static final Type OBJECT = new TypeToken<Object>() {
    }.getType();
    public static final Type ALT = new TypeToken<List<Alt>>() {
    }.getType();
    public static final Type PUNISHMENT = new TypeToken<Punishment>() {
    }.getType();
    public static final Type STRING_LIST = new TypeToken<List<String>>() {
    }.getType();
    public static final Type UUID_SET = new TypeToken<HashSet<UUID>>() {
    }.getType();
    public static final Type STRING_STRING_MAP = new TypeToken<Map<String, String>>() {
    }.getType();
}
