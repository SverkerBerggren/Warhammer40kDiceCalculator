package com.app.DamageCalculator40k;

import android.util.Log;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Abilities.UnimplementedAbility;
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

public class AbilityElementAdapter implements JsonSerializer<Ability>, JsonDeserializer<Ability> {
    private Gson internalGson = new Gson();

    @Override
    public JsonElement serialize(Ability src, Type typeOfSrc, JsonSerializationContext context) {

        JsonElement regularAbilityParsing =  internalGson.toJsonTree(src);
        JsonObject jsonObjectAbility = regularAbilityParsing.getAsJsonObject();
        jsonObjectAbility.add("type",new JsonPrimitive( src.getClass().getTypeName()));

        return jsonObjectAbility;
    }

    @Override
    public Ability deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Ability ability = null;
        try {
            Type classType = Class.forName(jsonObject.get("type").getAsString());
            jsonObject.remove("type");
            ability = internalGson.fromJson(jsonObject.toString(),classType);
        }
        catch (Exception e )
        {
            Log.d("Serialiserings knas",e.getMessage());
        }
        if(ability != null)
        {
            return ability;
        }
        else
        {
            String name = jsonObject.get("name").getAsString();
            ability = new UnimplementedAbility(name);
            return ability;
        }
    }
}
