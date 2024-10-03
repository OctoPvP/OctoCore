package net.octopvp.octocore.common.util.json;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Created by fdoyle on 3/20/15.
 */
public class OptionalTypeAdapter implements JsonDeserializer<Optional<?>>, JsonSerializer<Optional<?>> {

    @Override
    public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final Object value = context.deserialize(json, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
        return Optional.ofNullable(value);
    }

    @Override
    public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.orElse(null));
    }
}