package net.octopvp.aetheriacoremaster.websocket.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.badbird5907.blib.objects.tuple.Pair;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

import java.lang.reflect.InvocationTargetException;

public class PacketManager {
    private static final String packageBase = "net.octopvp.aetheriacoremaster.websocket.packet.impl";

    public static String serializePacket(Packet packet) {
        JsonObject json = new JsonObject();
        json.addProperty("name", packet.getClass().getSimpleName());
        json.addProperty("play", packet.getDirection().name());
        //json.add("data", AetheriaCoreCommon.getInstance().getGson().toJsonTree(packet));
        json.add("data", packet.serialize());
        return AetheriaCoreCommon.getInstance().getGson().toJson(json);
    }
    private static final Objenesis objenesis = new ObjenesisStd();

    public static Pair<PacketIn, JsonObject> deserializePacket(String str) {
        JsonObject jsonObject = JsonParser.parseString(str).getAsJsonObject();
        Class<? extends PacketIn> clazz;
        try {
            clazz = Class.forName(packageBase + ".in." + jsonObject.get("name").getAsString()).asSubclass(PacketIn.class);
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName(packageBase + "." + jsonObject.get("name").getAsString()).asSubclass(PacketIn.class);
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
        JsonElement data = jsonObject.get("data");
        if (data != null) {
            return new Pair<>(AetheriaCoreCommon.getInstance().getGson().fromJson(data, clazz), data.getAsJsonObject());
        } else { // data is null
            try {
                Object obj = clazz.getDeclaredConstructor().newInstance();
                return new Pair<>((PacketIn) obj, null);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                AetheriaCoreCommon.getInstance().getServerImplementation().logDebug("No constructor found for " + clazz.getSimpleName() + " attempting to use experimental Objenesis");
                ObjectInstantiator<?> instantiator = objenesis.getInstantiatorOf(clazz);
                return new Pair<>((PacketIn) instantiator.newInstance(), null);
            }
        }
    }
}
