package com.app.DamageCalculator40k.Identifiers;

import com.app.DamageCalculator40k.Enums.IdentifierType;
import com.app.DamageCalculator40k.Matchup;

import java.util.Objects;

public class ModelIdentifier extends Identifier{

    public String tag;
    public int indexUnit;
    public int indexModel;
    public String matchupName;

    public ModelIdentifier(String allegiance, String tag, int indexUnit, int indexModel, String matchupName)
    {
        this.allegiance = allegiance;
        this.tag = tag;
        this.indexUnit = indexUnit;
        this.indexModel = indexModel;
        this.matchupName = matchupName;
    }

    @Override
    public String toString() {
        return "ModelIdentifier{" +
                "allegiance=" + allegiance +
                ", tag=" + tag  +
                ", indexUnit=" + indexUnit +
                ", indexModel=" + indexModel +
                ", matchupName=" + matchupName  +
                '}';
    }

    @Override
    public IdentifierType GetIdentifierEnum() {
        return IdentifierType.MODEL;
    }

    // Chat gpt yikes
    public ModelIdentifier(String identifierString) {
        if (identifierString != null) {
            // Remove the "ModelIdentifier{" prefix and "}" suffix from the string
            identifierString = identifierString.replace("ModelIdentifier{", "").replace("}", "");

            // Split the string into key-value pairs
            String[] pairs = identifierString.split(", ");

            // Process each key-value pair
            for (String pair : pairs) {
                // Split the pair into key and value
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    // Assign the value to the corresponding field
                    switch (key) {
                        case "allegiance":
                            allegiance = value.equals("null") ? null : value;
                            break;
                        case "tag":
                            tag = value.equals("null") ? null : value;
                            break;
                        case "indexUnit":
                            indexUnit = Integer.parseInt(value);
                            break;
                        case "indexModel":
                            indexModel = Integer.parseInt(value);
                            break;
                        case "matchupName":
                            matchupName = value.equals("null") ? null : value;
                            break;
                    }
                }
            }
        }
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelIdentifier that = (ModelIdentifier) o;
        return indexUnit == that.indexUnit && indexModel == that.indexModel && Objects.equals(allegiance, that.allegiance) && Objects.equals(tag, that.tag) && Objects.equals(matchupName, that.matchupName);
    }

    @Override
    public String GetMatchupName() {
        return matchupName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(allegiance, tag, indexUnit, indexModel, matchupName);
    }
}
