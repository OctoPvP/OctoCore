package net.octopvp.octocore.common.mfa;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.mfa.impl.TOTPMFAProvider;

public interface MFAData {

    /**
     * Serializes the data to a {@link JsonObject}
     *
     * @return serialized data
     */
    JsonObject serialize();

    default JsonObject serializeFully() {
        JsonObject object = new JsonObject();
        object.addProperty("type", getType().name());
        object.add("data", serialize());
        return object;
    }

    MFAType getType();

    /**
     * Deserializes the data from a {@link JsonObject}
     *
     * @param object
     * @return deserialized data
     */
    MFAData deserialize(JsonObject object);

    default MFAData deserializeFully(JsonObject object) {
        if (object.has("type") && object.has("data")) {
            return deserialize(object.get("data").getAsJsonObject());
        }
        return deserialize(object);
    }

    static MFAData deserialize(MFAType type, JsonObject o) {
        JsonObject object = o.has("data") ? o.get("data").getAsJsonObject() : o;
        switch (type) {
            case TOTP:
                return new TOTPMFAProvider.TOTPMFAData().deserializeFully(object);
            case SECURITY_KEY:
                return null;
        }
        return null;
    }
}
