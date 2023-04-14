package com.example.warhammer40kdicecalculator;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AbilityElementAdapter implements JsonSerializer<Ability>, JsonDeserializer<Ability> {


    @Override
    public JsonElement serialize(Ability src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("name", new JsonPrimitive(src.name));
       // result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public Ability deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("name").getAsString();
        JsonElement element = jsonObject.get("name");

        Ability abilityToReturn = Ability.getAbilityType(type);


        return abilityToReturn;

    }


}