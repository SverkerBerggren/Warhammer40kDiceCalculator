package core;


import core.Abilities.Ability;
import core.Abilities.AbilityKind;
import core.Abilities.DualModeAbility;
import core.Abilities.UnimplementedAbility;
import core.Logging.Logging;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class AbilityElementAdapter implements JsonSerializer<Ability>, JsonDeserializer<Ability> {
    private Gson internalGson = new Gson();

    private static final Map<Class<? extends Ability>, AbilityKind> REVERSE_REGISTRY =
            Arrays.stream(AbilityKind.values())
                    .collect(Collectors.toMap(AbilityKind::abilityClass, k -> k));

    @Override
    public JsonElement serialize(Ability src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement tree = internalGson.toJsonTree(src);
        JsonObject obj = tree.getAsJsonObject();
        AbilityKind kind = REVERSE_REGISTRY.getOrDefault(src.getClass(), AbilityKind.UNIMPLEMENTED);
        obj.add("kind", new JsonPrimitive(kind.name()));
        obj.addProperty("implemented", src.isImplemented());
        obj.addProperty("hasBoostedVersion", (src instanceof DualModeAbility));
        return obj;
    }

    @Override
    public Ability deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject obj = json.getAsJsonObject();
        Class<? extends Ability> clazz;
        try {
            clazz = AbilityKind.valueOf(obj.get("kind").getAsString()).abilityClass();
        } catch (IllegalArgumentException e) {
            return new UnimplementedAbility(obj.get("name").getAsString());
        }
        obj.remove("kind");
        return internalGson.fromJson(obj, clazz);
    }
}
