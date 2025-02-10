package com.app.DamageCalculator40k.Identifiers;

import com.app.DamageCalculator40k.Enums.IdentifierType;

import java.util.Objects;

public class ArmyIdentifier extends Identifier{
    public String allegiance;
    public String matchupName;

    public ArmyIdentifier(String allegiance, String matchupName)
    {
        this.allegiance = allegiance;
        this.matchupName = matchupName;
    }

    @Override
    public String toString() {
        return "ArmyIdentifier{" +
                "allegiance=" + allegiance +
                ", matchupName= " + matchupName +
                '}';
    }

    @Override
    public IdentifierType GetIdentifierEnum() {
        return IdentifierType.ARMY;
    }


    public ArmyIdentifier(String identifierString) {
        if (identifierString != null) {
            // Remove the "ArmyIdentifier{" prefix and "}" suffix from the string
            identifierString = identifierString.replace("ArmyIdentifier{", "").replace("}", "");

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
        ArmyIdentifier that = (ArmyIdentifier) o;
        return Objects.equals(allegiance, that.allegiance) && Objects.equals(matchupName, that.matchupName);
    }

    @Override
    public String GetMatchupName() {
        return matchupName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(allegiance, matchupName);
    }
}
