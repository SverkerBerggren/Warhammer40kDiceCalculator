package com.app.DamageCalculator40k;

import android.util.Log;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class AbilityElementAdapter implements JsonSerializer<Ability>, JsonDeserializer<Ability> {
    private Gson internalGson = new Gson();

    @Override
    public JsonElement serialize(Ability src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("name", new JsonPrimitive(src.name));
        Object test = src.getClass().cast(src);

        return result;
    }

    @Override
    public Ability deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("name").getAsString();
        JsonElement element = jsonObject.get("name");

        return DatabaseManager.getInstance().GetAbility(type);
    }
}
